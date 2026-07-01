package com.shreyash.antitheft.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.shreyash.antitheft.data.EventLog
import com.shreyash.antitheft.util.NotificationHelper

class AlarmForegroundService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var wasCharging: Boolean = false
    private var isPolling = false

    private val pollRunnable = object : Runnable {
        override fun run() {
            if (!isPolling) return
            checkChargingState()
            handler.postDelayed(this, POLL_INTERVAL_MS)
        }
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        wasCharging = isCurrentlyCharging()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationHelper.buildNotification(this)
        startForeground(NOTIFICATION_ID, notification)
        if (!isPolling) {
            isPolling = true
            handler.post(pollRunnable)
        }
        return START_STICKY
    }

    private fun isCurrentlyCharging(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val bm = getSystemService(BATTERY_SERVICE) as? BatteryManager ?: return false
            return bm.isCharging
        }
        val sticky = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val plugged = sticky?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0
        return plugged == BatteryManager.BATTERY_PLUGGED_AC ||
               plugged == BatteryManager.BATTERY_PLUGGED_USB ||
               plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
    }

    private fun checkChargingState() {
        val nowCharging = isCurrentlyCharging()
        if (wasCharging && !nowCharging) {
            val prefs = PrefsManager(this)
            val eventLog = EventLog(this)
            prefs.lastDisconnectTime = System.currentTimeMillis()
            eventLog.addEvent("info", "Charger disconnected (service poll)")

            if (!prefs.isChargingGuardEnabled) return
            if (!prefs.isArmed) return

            eventLog.addEvent("alarm", "Charging Guard triggered")
            prefs.pendingAlarm = true
        }
        wasCharging = nowCharging
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        isPolling = false
        handler.removeCallbacks(pollRunnable)
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val POLL_INTERVAL_MS = 5_000L
    }
}

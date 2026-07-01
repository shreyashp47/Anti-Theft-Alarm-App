package com.shreyash.antitheft.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.shreyash.antitheft.receiver.ChargingReceiver
import com.shreyash.antitheft.util.NotificationHelper

class AlarmForegroundService : Service() {

    private var chargingReceiver: ChargingReceiver? = null

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        chargingReceiver = ChargingReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        registerReceiver(chargingReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationHelper.buildNotification(this)
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        chargingReceiver?.let { unregisterReceiver(it) }
        chargingReceiver = null
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}

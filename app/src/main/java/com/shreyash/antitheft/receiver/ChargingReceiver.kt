package com.shreyash.antitheft.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.shreyash.antitheft.R
import com.shreyash.antitheft.data.EventLog
import com.shreyash.antitheft.service.PrefsManager
import com.shreyash.antitheft.ui.alarm.AlarmActivity
import com.shreyash.antitheft.util.NotificationHelper

class ChargingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val prefs = PrefsManager(context)
        val eventLog = EventLog(context)

        when (action) {
            Intent.ACTION_POWER_DISCONNECTED -> {
                prefs.lastDisconnectTime = System.currentTimeMillis()
                eventLog.addEvent("info", "Charger disconnected")

                if (!prefs.isChargingGuardEnabled) return

                if (!prefs.isArmed) {
                    showNotification(context, "Charging Guard enabled but app is not armed")
                    return
                }

                eventLog.addEvent("alarm", "Charging Guard triggered")
                showAlarmNotification(context, prefs)
            }
            Intent.ACTION_POWER_CONNECTED -> {
                eventLog.addEvent("info", "Charger connected")
            }
        }
    }

    private fun showAlarmNotification(context: Context, prefs: PrefsManager) {
        NotificationHelper.createChannels(context)

        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra(EXTRA_ALARM_TYPE, ALARM_TYPE_CHARGING)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        try {
            context.startActivity(alarmIntent)
            return
        } catch (_: Exception) {
        }

        prefs.pendingAlarm = true

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, AlarmActivity::class.java).apply {
                putExtra(EXTRA_ALARM_TYPE, ALARM_TYPE_CHARGING)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.alarm_triggered_title))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
        }
    }

    private fun showNotification(context: Context, message: String) {
        try {
            NotificationHelper.createChannels(context)
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID + 1, notification)
        } catch (_: Exception) {
        }
    }

    companion object {
        const val EXTRA_ALARM_TYPE = "alarm_type"
        const val ALARM_TYPE_CHARGING = "charging"
        private const val CHANNEL_ID = "charging_guard_events"
        private const val NOTIFICATION_ID = 1001
    }
}

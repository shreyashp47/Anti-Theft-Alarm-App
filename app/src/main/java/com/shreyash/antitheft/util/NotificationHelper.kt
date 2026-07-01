package com.shreyash.antitheft.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.shreyash.antitheft.R

object NotificationHelper {
    const val CHANNEL_ID = "alarm_service"
    const val CHANNEL_EVENTS = "charging_guard_events"
    private const val NOTIFICATION_ID = 1

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.notification_channel_description)
            setShowBadge(false)
        }
        manager.createNotificationChannel(serviceChannel)

        val eventChannel = NotificationChannel(
            CHANNEL_EVENTS,
            "Charging Guard Events",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts for charging guard feature"
        }
        manager.createNotificationChannel(eventChannel)
    }

    fun buildNotification(context: Context): android.app.Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSilent(true)
            .build()
    }
}

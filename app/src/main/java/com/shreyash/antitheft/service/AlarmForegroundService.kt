package com.shreyash.antitheft.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.shreyash.antitheft.util.NotificationHelper

class AlarmForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationHelper.buildNotification(this)
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}

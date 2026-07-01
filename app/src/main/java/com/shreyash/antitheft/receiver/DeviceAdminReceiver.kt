package com.shreyash.antitheft.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.shreyash.antitheft.R

class DeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {}

    override fun onDisabled(context: Context, intent: Intent) {}

    override fun onLockTaskModeEntering(context: Context, intent: Intent, pin: String) {}

    override fun onLockTaskModeExiting(context: Context, intent: Intent) {}
}

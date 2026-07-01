package com.shreyash.antitheft.service

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isArmed: Boolean
        get() = prefs.getBoolean(KEY_ARMED, false)
        set(value) = prefs.edit().putBoolean(KEY_ARMED, value).apply()

    var isChargingGuardEnabled: Boolean
        get() = prefs.getBoolean(KEY_CHARGING_GUARD, true)
        set(value) = prefs.edit().putBoolean(KEY_CHARGING_GUARD, value).apply()

    var lastDisconnectTime: Long
        get() = prefs.getLong(KEY_LAST_DISCONNECT, -1L)
        set(value) = prefs.edit().putLong(KEY_LAST_DISCONNECT, value).apply()

    var pendingAlarm: Boolean
        get() = prefs.getBoolean(KEY_PENDING_ALARM, false)
        set(value) = prefs.edit().putBoolean(KEY_PENDING_ALARM, value).apply()

    companion object {
        private const val PREFS_NAME = "antitheft_prefs"
        private const val KEY_ARMED = "is_armed"
        private const val KEY_CHARGING_GUARD = "charging_guard_enabled"
        private const val KEY_LAST_DISCONNECT = "last_disconnect_time"
        private const val KEY_PENDING_ALARM = "pending_alarm"
    }
}

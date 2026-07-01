package com.shreyash.antitheft

import android.app.Application
import com.shreyash.antitheft.util.NotificationHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
    }
}

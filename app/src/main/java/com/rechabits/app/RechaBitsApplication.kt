package com.rechabits.app

import android.app.Application
import com.rechabits.app.data.reminder.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RechaBitsApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }
}

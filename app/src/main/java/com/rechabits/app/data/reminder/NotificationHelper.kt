package com.rechabits.app.data.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.rechabits.app.R

object NotificationHelper {
    
    private const val REMINDER_CHANNEL_ID = "rechabits_reminders"
    private const val OVERLAY_CHANNEL_ID = "rechabits_overlay_channel"
    
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "RechaBits Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Habit reminders with full-screen intent"
                setShowBadge(true)
                enableVibration(true)
            }
            
            val overlayChannel = NotificationChannel(
                OVERLAY_CHANNEL_ID,
                "RechaBits Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notification for overlay service"
                setShowBadge(false)
            }
            
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(reminderChannel)
            manager.createNotificationChannel(overlayChannel)
        }
    }
    
    fun createReminderNotification(
        context: Context,
        habitId: Long,
        habitName: String,
        iconId: String
    ): androidx.core.app.NotificationCompat.Builder {
        return NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("RechaBits")
            .setContentText("Es hora de tu hábito")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
    }
}

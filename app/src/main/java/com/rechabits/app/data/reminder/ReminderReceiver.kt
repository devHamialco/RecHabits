package com.rechabits.app.data.reminder

import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.rechabits.app.data.db.RechaBitsDatabase
import com.rechabits.app.data.repository.HabitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(EXTRA_HABIT_ID, -1L)
        val scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, -1L)

        if (habitId == -1L) return

        // Get habit details from database
        CoroutineScope(Dispatchers.IO).launch {
            val db = RechaBitsDatabase.getDatabase(context)
            val repo = HabitRepository(db.habitDao(), db.completionDao())
            val habit = repo.getById(habitId)

            if (habit == null) return@launch

            val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val isLocked = keyguardManager.isKeyguardLocked

            if (isLocked) {
                // Device is locked -> launch full-screen activity
                val reminderIntent = Intent(context, ReminderActivity::class.java).apply {
                    putExtra(EXTRA_HABIT_ID, habitId)
                    putExtra(EXTRA_SCHEDULE_ID, scheduleId)
                    putExtra(EXTRA_HABIT_NAME, habit.name)
                    putExtra(EXTRA_ICON_ID, habit.iconId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SHOW_WHEN_LOCKED
                }
                context.startActivity(reminderIntent)
            } else {
                // Device is unlocked -> try overlay or fallback to notification
                val hasOverlayPermission = PermissionHelper.hasOverlayPermission(context)

                if (hasOverlayPermission) {
                    val overlayIntent = Intent(context, OverlayService::class.java).apply {
                        putExtra(EXTRA_HABIT_ID, habitId)
                        putExtra(EXTRA_HABIT_NAME, habit.name)
                        putExtra(EXTRA_ICON_ID, habit.iconId)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(overlayIntent)
                    } else {
                        context.startService(overlayIntent)
                    }
                } else {
                    // Fallback to notification
                    showNotification(context, habitId, habit.name, habit.iconId)
                }
            }
        }
    }

    private fun showNotification(context: Context, habitId: Long, habitName: String, iconId: String) {
        val fullScreenIntent = Intent(context, ReminderActivity::class.java).apply {
            putExtra(EXTRA_HABIT_ID, habitId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            habitId.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationHelper.createReminderNotification(
            context = context,
            habitId = habitId,
            habitName = habitName,
            iconId = iconId
        )
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        val manager = context.getSystemService(android.app.NotificationManager::class.java)
        manager.notify(habitId.toInt(), notification)
    }

    companion object {
        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_SCHEDULE_ID = "extra_schedule_id"
        const val EXTRA_HABIT_NAME = "extra_habit_name"
        const val EXTRA_ICON_ID = "extra_icon_id"
    }
}

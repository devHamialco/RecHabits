package com.rechabits.app.data.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(EXTRA_HABIT_ID, -1L)
        val scheduleId = intent.getLongExtra(EXTRA_SCHEDULE_ID, -1L)

        if (habitId == -1L) return

        // Launch full-screen reminder activity
        val reminderIntent = Intent(context, ReminderActivity::class.java).apply {
            putExtra(EXTRA_HABIT_ID, habitId)
            putExtra(EXTRA_SCHEDULE_ID, scheduleId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(reminderIntent)
    }

    companion object {
        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_SCHEDULE_ID = "extra_schedule_id"
    }
}

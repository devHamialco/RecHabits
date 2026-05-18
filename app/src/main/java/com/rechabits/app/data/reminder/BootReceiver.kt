package com.rechabits.app.data.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rechabits.app.data.repository.ReminderRepository
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repository = EntryPointAccessors.fromApplication(
                context.applicationContext,
                ReminderRepositoryEntryPoint::class.java
            ).reminderRepository()

            CoroutineScope(Dispatchers.IO).launch {
                repository.registerAllAlarms()
            }
        }
    }
}

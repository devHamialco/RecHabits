package com.rechabits.app.data.reminder

import com.rechabits.app.data.repository.ReminderRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReminderRepositoryEntryPoint {
    fun reminderRepository(): ReminderRepository
}

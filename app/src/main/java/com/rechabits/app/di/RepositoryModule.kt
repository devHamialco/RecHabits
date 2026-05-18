package com.rechabits.app.di

import android.app.AlarmManager
import android.content.Context
import com.rechabits.app.data.db.dao.CompletionDao
import com.rechabits.app.data.db.dao.HabitDao
import com.rechabits.app.data.db.dao.ScheduleDao
import com.rechabits.app.data.repository.HabitRepository
import com.rechabits.app.data.repository.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHabitRepository(
        habitDao: HabitDao,
        completionDao: CompletionDao
    ): HabitRepository {
        return HabitRepository(habitDao, completionDao)
    }

    @Provides
    @Singleton
    fun provideReminderRepository(
        scheduleDao: ScheduleDao,
        alarmManager: AlarmManager,
        @ApplicationContext context: Context
    ): ReminderRepository {
        return ReminderRepository(scheduleDao, alarmManager, context)
    }
}

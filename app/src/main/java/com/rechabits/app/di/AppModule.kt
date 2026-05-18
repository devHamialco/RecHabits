package com.rechabits.app.di

import android.app.AlarmManager
import android.content.Context
import androidx.room.Room
import com.rechabits.app.data.db.RechaBitsDatabase
import com.rechabits.app.data.db.dao.CompletionDao
import com.rechabits.app.data.db.dao.HabitDao
import com.rechabits.app.data.db.dao.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RechaBitsDatabase {
        return Room.databaseBuilder(
            context,
            RechaBitsDatabase::class.java,
            "rechabits_database"
        ).build()
    }

    @Provides
    fun provideHabitDao(database: RechaBitsDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    fun provideCompletionDao(database: RechaBitsDatabase): CompletionDao {
        return database.completionDao()
    }

    @Provides
    fun provideScheduleDao(database: RechaBitsDatabase): ScheduleDao {
        return database.scheduleDao()
    }

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
}

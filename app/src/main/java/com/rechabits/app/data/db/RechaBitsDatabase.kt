package com.rechabits.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rechabits.app.data.db.dao.CompletionDao
import com.rechabits.app.data.db.dao.HabitDao
import com.rechabits.app.data.db.dao.ScheduleDao
import com.rechabits.app.data.db.entity.CompletionEntity
import com.rechabits.app.data.db.entity.HabitEntity
import com.rechabits.app.data.db.entity.ScheduleEntity

@Database(
    entities = [HabitEntity::class, CompletionEntity::class, ScheduleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RechaBitsDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun completionDao(): CompletionDao
    abstract fun scheduleDao(): ScheduleDao
}

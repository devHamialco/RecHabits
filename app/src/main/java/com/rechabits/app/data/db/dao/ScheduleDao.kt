package com.rechabits.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rechabits.app.data.db.entity.ScheduleEntity

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE enabled = 1")
    suspend fun getAllEnabled(): List<ScheduleEntity>

    @Query("SELECT * FROM schedules WHERE habitId = :habitId ORDER BY hour ASC, minute ASC")
    suspend fun getByHabitId(habitId: Long): List<ScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleEntity)

    @Query("DELETE FROM schedules WHERE habitId = :habitId")
    suspend fun deleteByHabitId(habitId: Long)
}

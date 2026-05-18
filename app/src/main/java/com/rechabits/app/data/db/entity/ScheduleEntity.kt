package com.rechabits.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "schedules", indices = [Index("habitId")])
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = true
)

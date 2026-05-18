package com.rechabits.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "completions", indices = [Index("habitId", "date")])
data class CompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val date: String,
    val amount: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)

package com.rechabits.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconId: String,
    val colorHex: String,
    val frequencyType: String,
    val targetAmount: Int,
    val unit: String,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

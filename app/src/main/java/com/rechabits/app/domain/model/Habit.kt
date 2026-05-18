package com.rechabits.app.domain.model

data class Habit(
    val id: Long = 0,
    val name: String,
    val iconId: String,
    val colorHex: String,
    val frequencyType: String,
    val targetAmount: Int,
    val unit: String,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isBinary: Boolean
        get() = targetAmount <= 1
}

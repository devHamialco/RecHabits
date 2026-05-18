package com.rechabits.app.domain.model

data class Completion(
    val id: Long = 0,
    val habitId: Long,
    val date: String,
    val amount: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)

package com.rechabits.app.domain.model

data class Schedule(
    val id: Long = 0,
    val habitId: Long,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = true
) {
    fun toTimeString(): String = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

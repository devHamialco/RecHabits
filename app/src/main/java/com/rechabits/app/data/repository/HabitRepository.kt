package com.rechabits.app.data.repository

import com.rechabits.app.data.db.dao.CompletionDao
import com.rechabits.app.data.db.dao.HabitDao
import com.rechabits.app.data.db.entity.CompletionEntity
import com.rechabits.app.data.db.entity.HabitEntity
import com.rechabits.app.domain.model.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class HabitRepository @Inject constructor(
    private val habitDao: HabitDao,
    private val completionDao: CompletionDao
) {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getAllActive(): Flow<List<Habit>> {
        return habitDao.getAllActive().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getById(id: Long): Habit? {
        return habitDao.getById(id)?.toDomain()
    }

    suspend fun insert(habit: Habit): Long {
        return habitDao.insert(habit.toEntity())
    }

    suspend fun update(habit: Habit) {
        habitDao.update(habit.toEntity())
    }

    suspend fun delete(habit: Habit) {
        habitDao.delete(habit.toEntity())
        completionDao.deleteByHabitId(habit.id)
    }

    suspend fun completeHabit(habitId: Long, amount: Int = 1) {
        val today = LocalDate.now().format(dateFormatter)
        val completion = CompletionEntity(
            habitId = habitId,
            date = today,
            amount = amount
        )
        completionDao.insert(completion)
    }

    suspend fun getTodayAmount(habitId: Long): Int {
        val today = LocalDate.now().format(dateFormatter)
        return completionDao.getTotalAmountForDate(habitId, today)
    }

    private fun HabitEntity.toDomain() = Habit(
        id = id,
        name = name,
        iconId = iconId,
        colorHex = colorHex,
        frequencyType = frequencyType,
        targetAmount = targetAmount,
        unit = unit,
        enabled = enabled,
        createdAt = createdAt
    )

    private fun Habit.toEntity() = HabitEntity(
        id = id,
        name = name,
        iconId = iconId,
        colorHex = colorHex,
        frequencyType = frequencyType,
        targetAmount = targetAmount,
        unit = unit,
        enabled = enabled,
        createdAt = createdAt
    )
}

package com.rechabits.app.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rechabits.app.data.repository.HabitRepository
import com.rechabits.app.data.repository.ReminderRepository
import com.rechabits.app.domain.model.Habit
import com.rechabits.app.domain.model.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HabitFormState(
    val name: String = "",
    val iconId: String = "water_drop",
    val colorHex: String = "#6650a4",
    val frequencyType: String = "daily",
    val targetAmount: Int = 1,
    val unit: String = "",
    val schedules: List<Schedule> = emptyList(),
    val isEditing: Boolean = false,
    val habitId: Long = 0
)

data class HabitEditUiState(
    val formState: HabitFormState = HabitFormState(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HabitEditViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val reminderRepository: ReminderRepository
) : ViewModel() {

    var uiState by mutableStateOf(HabitEditUiState())
        private set

    fun loadHabit(habitId: Long) {
        viewModelScope.launch {
            val habit = habitRepository.getById(habitId)
            val schedules = reminderRepository.getSchedulesForHabit(habitId)
            if (habit != null) {
                uiState = uiState.copy(
                    formState = HabitFormState(
                        name = habit.name,
                        iconId = habit.iconId,
                        colorHex = habit.colorHex,
                        frequencyType = habit.frequencyType,
                        targetAmount = habit.targetAmount,
                        unit = habit.unit,
                        schedules = schedules,
                        isEditing = true,
                        habitId = habit.id
                    )
                )
            }
        }
    }

    fun updateName(name: String) {
        uiState = uiState.copy(formState = uiState.formState.copy(name = name))
    }

    fun updateIcon(iconId: String) {
        uiState = uiState.copy(formState = uiState.formState.copy(iconId = iconId))
    }

    fun updateColor(colorHex: String) {
        uiState = uiState.copy(formState = uiState.formState.copy(colorHex = colorHex))
    }

    fun updateFrequency(frequencyType: String) {
        uiState = uiState.copy(formState = uiState.formState.copy(frequencyType = frequencyType))
    }

    fun updateTargetAmount(amount: Int) {
        uiState = uiState.copy(formState = uiState.formState.copy(targetAmount = amount))
    }

    fun updateUnit(unit: String) {
        uiState = uiState.copy(formState = uiState.formState.copy(unit = unit))
    }

    fun addSchedule(hour: Int, minute: Int) {
        val newSchedule = Schedule(hour = hour, minute = minute)
        val updated = uiState.formState.schedules + newSchedule
        uiState = uiState.copy(formState = uiState.formState.copy(schedules = updated))
    }

    fun removeSchedule(schedule: Schedule) {
        val updated = uiState.formState.schedules - schedule
        uiState = uiState.copy(formState = uiState.formState.copy(schedules = updated))
    }

    fun saveHabit() {
        val form = uiState.formState
        if (form.name.isBlank()) {
            uiState = uiState.copy(error = "El nombre es requerido")
            return
        }

        uiState = uiState.copy(isSaving = true, error = null)

        viewModelScope.launch {
            try {
                val habit = Habit(
                    id = if (form.isEditing) form.habitId else 0,
                    name = form.name.trim(),
                    iconId = form.iconId,
                    colorHex = form.colorHex,
                    frequencyType = form.frequencyType,
                    targetAmount = form.targetAmount,
                    unit = form.unit
                )

                val habitId = if (form.isEditing) {
                    habitRepository.update(habit)
                    form.habitId
                } else {
                    habitRepository.insert(habit)
                }

                // Update schedules
                if (form.isEditing) {
                    reminderRepository.deleteSchedulesForHabit(habitId)
                }
                form.schedules.forEach { schedule ->
                    reminderRepository.saveSchedule(
                        schedule.copy(habitId = habitId)
                    )
                }

                // Register alarms for new schedules
                reminderRepository.registerAllAlarms()

                uiState = uiState.copy(isSaving = false, saveSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    error = "Error al guardar: ${e.message}"
                )
            }
        }
    }

    fun resetSaveSuccess() {
        uiState = uiState.copy(saveSuccess = false)
    }
}

package com.rechabits.app.presentation.ui.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rechabits.app.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    fun completeHabit(habitId: Long) {
        viewModelScope.launch {
            habitRepository.completeHabit(habitId)
        }
    }
}

package com.rechabits.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rechabits.app.data.repository.HabitRepository
import com.rechabits.app.domain.model.Habit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HabitUiState(
    val habits: List<Habit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    val uiState: StateFlow<HabitUiState> = habitRepository.getAllActive()
        .map { habits ->
            HabitUiState(habits = habits, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HabitUiState(isLoading = true)
        )

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.delete(habit)
        }
    }
}

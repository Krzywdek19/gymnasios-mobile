package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutSessionHistoryViewModel(
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutSessionHistoryUiState>(
        WorkoutSessionHistoryUiState.Loading
    )
    val uiState: StateFlow<WorkoutSessionHistoryUiState> = _uiState.asStateFlow()

    fun loadWorkoutSessions() {
        viewModelScope.launch {
            _uiState.value = WorkoutSessionHistoryUiState.Loading

            workoutSessionRepository
                .getWorkoutSessions()
                .onSuccess { sessions ->
                    _uiState.value = WorkoutSessionHistoryUiState.Success(
                        sessions = sessions
                    )
                }
                .onFailure {
                    _uiState.value = WorkoutSessionHistoryUiState.Error(
                        messageRes = R.string.error_workout_session_history_load_failed
                    )
                }
        }
    }
}
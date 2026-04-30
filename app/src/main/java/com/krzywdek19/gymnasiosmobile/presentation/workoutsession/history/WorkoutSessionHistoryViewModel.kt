package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSessionStatus
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

    fun deleteWorkoutSession(workoutSessionId: String) {
        val currentState = currentSuccessOrNull() ?: return

        _uiState.value = currentState.copy(
            deletingSessionIds = currentState.deletingSessionIds + workoutSessionId,
            actionErrorMessageRes = null
        )

        viewModelScope.launch {
            workoutSessionRepository
                .deleteWorkoutSession(workoutSessionId)
                .onSuccess {
                    val latestState = currentSuccessOrNull() ?: return@onSuccess

                    _uiState.value = latestState.copy(
                        sessions = latestState.sessions.filterNot { it.id == workoutSessionId },
                        deletingSessionIds = latestState.deletingSessionIds - workoutSessionId,
                        actionErrorMessageRes = null
                    )
                }
                .onFailure {
                    val latestState = currentSuccessOrNull() ?: return@onFailure

                    _uiState.value = latestState.copy(
                        deletingSessionIds = latestState.deletingSessionIds - workoutSessionId,
                        actionErrorMessageRes = R.string.error_delete_workout_session_failed
                    )
                }
        }
    }

    fun clearFinishedWorkoutHistory() {
        val currentState = currentSuccessOrNull() ?: return

        _uiState.value = currentState.copy(
            isClearingHistory = true,
            actionErrorMessageRes = null
        )

        viewModelScope.launch {
            workoutSessionRepository
                .deleteFinishedWorkoutSessions()
                .onSuccess {
                    val latestState = currentSuccessOrNull() ?: return@onSuccess

                    _uiState.value = latestState.copy(
                        sessions = latestState.sessions.filterNot {
                            it.status == WorkoutSessionStatus.FINISHED
                        },
                        isClearingHistory = false,
                        actionErrorMessageRes = null
                    )
                }
                .onFailure {
                    val latestState = currentSuccessOrNull() ?: return@onFailure

                    _uiState.value = latestState.copy(
                        isClearingHistory = false,
                        actionErrorMessageRes = R.string.error_clear_workout_history_failed
                    )
                }
        }
    }

    private fun currentSuccessOrNull(): WorkoutSessionHistoryUiState.Success? {
        return _uiState.value as? WorkoutSessionHistoryUiState.Success
    }
}
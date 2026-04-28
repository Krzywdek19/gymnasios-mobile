package com.krzywdek19.gymnasiosmobile.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.repository.TrainingPlanRepository
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val trainingPlanRepository: TrainingPlanRepository,
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(
        DashboardUiState.Loading
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading

            try {
                val activePlan = trainingPlanRepository.getActiveTrainingPlan()

                val activeSession = workoutSessionRepository
                    .getActiveWorkoutSession()
                    .getOrNull()

                if (activeSession != null) {
                    _uiState.value = DashboardUiState.Success(
                        activePlanId = activePlan.id,
                        activePlanName = activePlan.name,
                        workoutName = activeSession.workoutTemplateName,
                        activeWorkoutSessionId = activeSession.id,
                        canStartWorkout = true
                    )
                    return@launch
                }

                val nextWorkout = workoutSessionRepository
                    .getNextWorkoutTemplate()
                    .getOrNull()

                _uiState.value = DashboardUiState.Success(
                    activePlanId = activePlan.id,
                    activePlanName = activePlan.name,
                    workoutName = nextWorkout?.name.orEmpty(),
                    activeWorkoutSessionId = null,
                    canStartWorkout = nextWorkout != null
                )
            } catch (_: Exception) {
                _uiState.value = DashboardUiState.Error(
                    messageRes = R.string.error_dashboard_load_failed
                )
            }
        }
    }

    fun startOrContinueWorkout(
        onWorkoutSessionReady: (String) -> Unit
    ) {
        val currentState = _uiState.value as? DashboardUiState.Success ?: return

        currentState.activeWorkoutSessionId?.let { sessionId ->
            onWorkoutSessionReady(sessionId)
            return
        }

        if (!currentState.canStartWorkout) return

        _uiState.value = currentState.copy(
            isStartingWorkout = true,
            actionErrorMessageRes = null
        )

        viewModelScope.launch {
            workoutSessionRepository
                .startNextWorkoutSession()
                .onSuccess { session ->
                    _uiState.value = currentState.copy(
                        workoutName = session.workoutTemplateName,
                        activeWorkoutSessionId = session.id,
                        isStartingWorkout = false,
                        actionErrorMessageRes = null
                    )

                    onWorkoutSessionReady(session.id)
                }
                .onFailure {
                    val latestState = _uiState.value as? DashboardUiState.Success ?: return@onFailure

                    val activeSession = workoutSessionRepository
                        .getActiveWorkoutSession()
                        .getOrNull()

                    if (activeSession != null) {
                        _uiState.value = latestState.copy(
                            workoutName = activeSession.workoutTemplateName,
                            activeWorkoutSessionId = activeSession.id,
                            isStartingWorkout = false,
                            actionErrorMessageRes = null
                        )

                        onWorkoutSessionReady(activeSession.id)
                    } else {
                        _uiState.value = latestState.copy(
                            isStartingWorkout = false,
                            actionErrorMessageRes = R.string.error_start_workout_session_failed
                        )
                    }
                }
        }
    }
}
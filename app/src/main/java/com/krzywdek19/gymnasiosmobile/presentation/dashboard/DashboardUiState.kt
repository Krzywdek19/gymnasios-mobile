package com.krzywdek19.gymnasiosmobile.presentation.dashboard

import androidx.annotation.StringRes

sealed interface DashboardUiState {

    data object Loading : DashboardUiState

    data class Success(
        val activePlanId: String?,
        val activePlanName: String,
        val workoutName: String,
        val activeWorkoutSessionId: String?,
        val canStartWorkout: Boolean,
        val isStartingWorkout: Boolean = false,
        @StringRes val actionErrorMessageRes: Int? = null
    ) : DashboardUiState

    data class Error(
        @StringRes val messageRes: Int
    ) : DashboardUiState
}
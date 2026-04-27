package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.active

import androidx.annotation.StringRes
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSession

sealed interface WorkoutSessionUiState {

    data object Loading : WorkoutSessionUiState

    data class Success(
        val session: WorkoutSession,
        val savingSetIds: Set<String> = emptySet(),
        val isFinishingSession: Boolean = false,
        @StringRes val actionErrorMessageRes: Int? = null
    ) : WorkoutSessionUiState

    data class Error(
        @StringRes val messageRes: Int
    ) : WorkoutSessionUiState
}
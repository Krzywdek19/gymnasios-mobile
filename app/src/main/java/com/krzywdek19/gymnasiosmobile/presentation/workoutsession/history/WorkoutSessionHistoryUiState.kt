package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.history

import androidx.annotation.StringRes
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSession

sealed interface WorkoutSessionHistoryUiState {

    data object Loading : WorkoutSessionHistoryUiState

    data class Success(
        val sessions: List<WorkoutSession>
    ) : WorkoutSessionHistoryUiState

    data class Error(
        @StringRes val messageRes: Int
    ) : WorkoutSessionHistoryUiState
}
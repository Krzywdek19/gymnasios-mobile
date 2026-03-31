package com.krzywdek19.gymnasiosmobile.presentation.trainingplan

import androidx.annotation.StringRes
import com.krzywdek19.gymnasiosmobile.domain.model.TrainingPlan

sealed interface TrainingPlanUiState {
    data object Loading : TrainingPlanUiState
    data class Success(val plans: List<TrainingPlan>) : TrainingPlanUiState
    data class Error(@StringRes val messageRes: Int) : TrainingPlanUiState
}
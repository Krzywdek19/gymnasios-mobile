package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details

import androidx.annotation.StringRes
import com.krzywdek19.gymnasiosmobile.domain.model.TrainingPlan

sealed interface TrainingPlanDetailsUiState {
    data object Loading : TrainingPlanDetailsUiState
    data class Success(val plan: TrainingPlan) : TrainingPlanDetailsUiState
    data class Error(@StringRes val messageRes: Int) : TrainingPlanDetailsUiState
}
package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.repository.TrainingPlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingPlanDetailsViewModel(
    private val repository: TrainingPlanRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<TrainingPlanDetailsUiState>(
            TrainingPlanDetailsUiState.Loading
        )
    val uiState: StateFlow<TrainingPlanDetailsUiState> = _uiState

    fun loadPlan(id: String) {
        viewModelScope.launch {
            _uiState.value = TrainingPlanDetailsUiState.Loading

            try {
                val plan = repository.getTrainingPlanById(id)
                _uiState.value = TrainingPlanDetailsUiState.Success(plan)
            } catch (e: Exception) {
                _uiState.value = TrainingPlanDetailsUiState.Error(
                    R.string.error_generic
                )
            }
        }
    }
}
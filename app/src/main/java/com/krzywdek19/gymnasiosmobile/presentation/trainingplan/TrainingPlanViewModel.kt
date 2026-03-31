package com.krzywdek19.gymnasiosmobile.presentation.trainingplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.repository.TrainingPlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingPlanViewModel(
    private val repository: TrainingPlanRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<TrainingPlanUiState>(TrainingPlanUiState.Loading)
    val uiState: StateFlow<TrainingPlanUiState> = _uiState

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.value = TrainingPlanUiState.Loading

            try {
                val plans = repository.getTrainingPlans()
                _uiState.value = TrainingPlanUiState.Success(plans)
            } catch (e: Exception) {
                _uiState.value = TrainingPlanUiState.Error(
                    R.string.error_generic
                )
            }
        }
    }
}
package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.repository.TrainingPlanRepository
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutTemplateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrainingPlanDetailsViewModel(
    private val repository: TrainingPlanRepository,
    private val workoutTemplateRepository: WorkoutTemplateRepository
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

    fun addWorkout(name: String) {
        val currentState = _uiState.value
        if (currentState !is TrainingPlanDetailsUiState.Success) return

        val plan = currentState.plan
        val nextOrderIndex = (plan.workouts.maxOfOrNull { it.orderIndex } ?: 0) + 1

        viewModelScope.launch {
            try {
                workoutTemplateRepository.createWorkoutTemplate(
                    planId = plan.id,
                    name = name,
                    orderIndex = nextOrderIndex
                )
                loadPlan(plan.id)
            } catch (e: Exception) {
                _uiState.value = TrainingPlanDetailsUiState.Error(
                    R.string.error_generic
                )
            }
        }
    }

    fun deleteWorkout(workoutId: String) {
        val currentState = _uiState.value
        if (currentState !is TrainingPlanDetailsUiState.Success) return

        val planId = currentState.plan.id

        viewModelScope.launch {
            try {
                workoutTemplateRepository.deleteWorkoutTemplateById(workoutId)
                loadPlan(planId)
            }catch (e: Exception) {
                _uiState.value = TrainingPlanDetailsUiState.Error(
                    R.string.error_generic
                )
            }
        }
    }
}
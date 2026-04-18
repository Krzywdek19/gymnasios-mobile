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

    fun renameWorkout(workoutId: String, newName: String) {
        val currentState = _uiState.value
        if (currentState !is TrainingPlanDetailsUiState.Success) return

        val plan = currentState.plan
        val workout = plan.workouts.firstOrNull { it.id == workoutId } ?: return

        viewModelScope.launch {
            try {
                workoutTemplateRepository.updateWorkoutTemplate(
                    workoutId = workout.id,
                    name = newName,
                    order = workout.orderIndex
                )
                loadPlan(plan.id)
            } catch (e: Exception) {
                _uiState.value = TrainingPlanDetailsUiState.Error(
                    R.string.error_generic
                )
            }
        }
    }

    fun moveWorkoutUp(workoutId: String) {
        moveWorkout(workoutId, -1)
    }

    fun moveWorkoutDown(workoutId: String) {
        moveWorkout(workoutId, 1)
    }

    private fun moveWorkout(workoutId: String, delta: Int) {
        val currentState = _uiState.value
        if (currentState !is TrainingPlanDetailsUiState.Success) return

        val plan = currentState.plan
        val sortedWorkouts = plan.workouts.sortedBy { it.orderIndex }
        val workout = sortedWorkouts.firstOrNull { it.id == workoutId } ?: return

        val newOrder = workout.orderIndex + delta
        if (newOrder < 1 || newOrder > sortedWorkouts.size) return

        viewModelScope.launch {
            try {
                workoutTemplateRepository.updateWorkoutTemplate(
                    workoutId = workout.id,
                    name = workout.name,
                    order = newOrder
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
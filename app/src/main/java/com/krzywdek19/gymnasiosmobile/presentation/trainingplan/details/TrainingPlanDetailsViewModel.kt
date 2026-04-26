package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.model.TrainingPlan
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutTemplate
import com.krzywdek19.gymnasiosmobile.domain.repository.TrainingPlanRepository
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutTemplateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrainingPlanDetailsViewModel(
    private val repository: TrainingPlanRepository,
    private val workoutTemplateRepository: WorkoutTemplateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrainingPlanDetailsUiState>(
        TrainingPlanDetailsUiState.Loading
    )
    val uiState: StateFlow<TrainingPlanDetailsUiState> = _uiState.asStateFlow()

    fun loadPlan(id: String) {
        refreshPlan(id = id, showLoading = true)
    }

    private fun refreshPlan(
        id: String,
        showLoading: Boolean
    ) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = TrainingPlanDetailsUiState.Loading
            }

            try {
                val plan = repository.getTrainingPlanById(id)
                _uiState.value = TrainingPlanDetailsUiState.Success(plan)
            } catch (_: Exception) {
                _uiState.value = TrainingPlanDetailsUiState.Error(
                    R.string.error_generic
                )
            }
        }
    }

    fun addWorkout(name: String) {
        val currentPlan = currentPlanOrNull() ?: return
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return

        val nextOrderIndex = (currentPlan.workouts.maxOfOrNull { it.orderIndex } ?: 0) + 1

        viewModelScope.launch {
            try {
                workoutTemplateRepository.createWorkoutTemplate(
                    planId = currentPlan.id,
                    name = trimmedName,
                    orderIndex = nextOrderIndex
                )
                refreshPlan(currentPlan.id, showLoading = false)
            } catch (_: Exception) {
                _uiState.value = TrainingPlanDetailsUiState.Success(currentPlan)
            }
        }
    }

    fun renameWorkout(workoutId: String, newName: String) {
        val currentPlan = currentPlanOrNull() ?: return
        val workout = currentPlan.findWorkout(workoutId) ?: return
        val trimmedName = newName.trim()

        if (trimmedName.isBlank() || trimmedName == workout.name) return

        viewModelScope.launch {
            try {
                workoutTemplateRepository.updateWorkoutTemplate(
                    workoutId = workout.id,
                    name = trimmedName,
                    order = workout.orderIndex
                )
                refreshPlan(currentPlan.id, showLoading = false)
            } catch (_: Exception) {
                _uiState.value = TrainingPlanDetailsUiState.Success(currentPlan)
            }
        }
    }

    fun reorderWorkout(
        workoutId: String,
        newOrder: Int
    ) {
        val currentPlan = currentPlanOrNull() ?: return
        val workout = currentPlan.findWorkout(workoutId) ?: return

        if (newOrder !in 1..currentPlan.workouts.size) return
        if (workout.orderIndex == newOrder) return

        viewModelScope.launch {
            try {
                workoutTemplateRepository.updateWorkoutTemplate(
                    workoutId = workout.id,
                    name = workout.name,
                    order = newOrder
                )
                refreshPlan(currentPlan.id, showLoading = false)
            } catch (_: Exception) {
                _uiState.value = TrainingPlanDetailsUiState.Success(currentPlan)
            }
        }
    }

    fun deleteWorkout(workoutId: String) {
        val currentPlan = currentPlanOrNull() ?: return
        if (currentPlan.findWorkout(workoutId) == null) return

        viewModelScope.launch {
            try {
                workoutTemplateRepository.deleteWorkoutTemplateById(workoutId)
                refreshPlan(currentPlan.id, showLoading = false)
            } catch (_: Exception) {
                _uiState.value = TrainingPlanDetailsUiState.Success(currentPlan)
            }
        }
    }

    private fun currentPlanOrNull(): TrainingPlan? {
        return (_uiState.value as? TrainingPlanDetailsUiState.Success)?.plan
    }

    private fun TrainingPlan.findWorkout(workoutId: String): WorkoutTemplate? {
        return workouts.firstOrNull { it.id == workoutId }
    }

    fun renamePlan(
        planId: String,
        newName: String
    ) {
        val trimmedName = newName.trim()
        if (trimmedName.isEmpty()) return

        viewModelScope.launch {
            try {
                repository.updateTrainingPlan(planId, trimmedName)
                refreshPlan(planId, showLoading = false)
            } catch (e: Exception) {
                _uiState.value = TrainingPlanDetailsUiState.Error(R.string.error_generic)
            }
        }
    }

    fun deletePlan(
        planId: String,
        onDeleted: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.deleteTrainingPlan(planId)
                onDeleted()
            } catch (e: Exception) {
                _uiState.value = TrainingPlanDetailsUiState.Error(R.string.error_generic)
            }
        }
    }
}


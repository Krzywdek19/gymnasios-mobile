package com.krzywdek19.gymnasiosmobile.presentation.workouttemplate.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.domain.repository.ExerciseTemplateRepository
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutTemplateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutTemplateDetailsViewModel(
    private val workoutTemplateRepository: WorkoutTemplateRepository,
    private val exerciseTemplateRepository: ExerciseTemplateRepository,
    private val workoutId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutTemplateDetailsUiState(isLoading = true))
    val uiState: StateFlow<WorkoutTemplateDetailsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val workout = workoutTemplateRepository.getWorkoutById(workoutId)
                val exercises = exerciseTemplateRepository
                    .getExercisesByWorkout(workoutId)
                    .sortedBy { it.orderIndex }

                _uiState.value = WorkoutTemplateDetailsUiState(
                    isLoading = false,
                    workout = workout,
                    exercises = exercises
                )
            } catch (e: Exception) {
                _uiState.value = WorkoutTemplateDetailsUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun reorderExercise(
        exerciseId: String,
        newOrder: Int
    ) {
        val currentState = _uiState.value
        val exercise = currentState.exercises.firstOrNull { it.id == exerciseId } ?: return

        if (exercise.orderIndex == newOrder) return

        viewModelScope.launch {
            try {
                exerciseTemplateRepository.updateExercise(
                    id = exercise.id,
                    name = exercise.name,
                    setsCount = exercise.setsCount,
                    reps = exercise.reps,
                    orderIndex = newOrder,
                    notes = exercise.notes ?: ""
                )
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }
}
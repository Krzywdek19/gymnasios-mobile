package com.krzywdek19.gymnasiosmobile.presentation.workouttemplate.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseTemplate
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
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                screenErrorMessageRes = null,
                actionErrorMessageRes = null
            )

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
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    screenErrorMessageRes = R.string.error_workout_details_failed
                )
            }
        }
    }

    fun reorderExercise(
        exerciseId: String,
        newOrder: Int
    ) {
        val currentState = _uiState.value
        val currentExercises = currentState.exercises
        val exercise = currentExercises.firstOrNull { it.id == exerciseId } ?: return

        if (exercise.orderIndex == newOrder) return
        if (newOrder < 1 || newOrder > currentExercises.size) return

        val reorderedExercises = reorderLocally(
            exercises = currentExercises,
            exerciseId = exerciseId,
            newOrder = newOrder
        )

        _uiState.value = currentState.copy(
            exercises = reorderedExercises,
            actionErrorMessageRes = null
        )

        viewModelScope.launch {
            try {
                exerciseTemplateRepository.updateExercise(
                    id = exercise.id,
                    name = exercise.name,
                    setsCount = exercise.setsCount,
                    reps = exercise.reps,
                    orderIndex = newOrder,
                    notes = exercise.notes
                )
                loadData()
            } catch (_: Exception) {
                _uiState.value = currentState.copy(
                    actionErrorMessageRes = R.string.error_workout_details_failed
                )
            }
        }
    }

    fun clearActionError() {
        _uiState.value = _uiState.value.copy(actionErrorMessageRes = null)
    }

    private fun reorderLocally(
        exercises: List<ExerciseTemplate>,
        exerciseId: String,
        newOrder: Int
    ): List<ExerciseTemplate> {
        val sorted = exercises.sortedBy { it.orderIndex }.toMutableList()
        val currentIndex = sorted.indexOfFirst { it.id == exerciseId }
        if (currentIndex == -1) return exercises

        val movedExercise = sorted.removeAt(currentIndex)
        sorted.add(newOrder - 1, movedExercise)

        return sorted.mapIndexed { index, item ->
            item.copy(orderIndex = index + 1)
        }
    }
}
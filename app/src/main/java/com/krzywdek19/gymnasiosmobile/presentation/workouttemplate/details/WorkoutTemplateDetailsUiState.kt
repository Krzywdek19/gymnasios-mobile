package com.krzywdek19.gymnasiosmobile.presentation.workouttemplate.details

import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseTemplate
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutTemplate

data class WorkoutTemplateDetailsUiState(
    val isLoading: Boolean = false,
    val workout: WorkoutTemplate? = null,
    val exercises: List<ExerciseTemplate> = emptyList(),
    val errorMessage: String? = null
)
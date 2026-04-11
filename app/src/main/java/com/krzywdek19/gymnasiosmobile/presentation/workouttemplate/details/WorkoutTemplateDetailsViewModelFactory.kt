package com.krzywdek19.gymnasiosmobile.presentation.workouttemplate.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krzywdek19.gymnasiosmobile.domain.repository.ExerciseTemplateRepository
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutTemplateRepository

class WorkoutTemplateDetailsViewModelFactory(
    private val workoutTemplateRepository: WorkoutTemplateRepository,
    private val exerciseTemplateRepository: ExerciseTemplateRepository,
    private val workoutId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutTemplateDetailsViewModel::class.java)) {
            return WorkoutTemplateDetailsViewModel(
                workoutTemplateRepository = workoutTemplateRepository,
                exerciseTemplateRepository = exerciseTemplateRepository,
                workoutId = workoutId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
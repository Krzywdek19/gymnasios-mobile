package com.krzywdek19.gymnasiosmobile.presentation.exercisetemplate.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krzywdek19.gymnasiosmobile.domain.repository.ExerciseTemplateRepository

class CreateExerciseTemplateViewModelFactory(
    private val exerciseTemplateRepository: ExerciseTemplateRepository,
    private val workoutTemplateId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateExerciseTemplateViewModel::class.java)) {
            return CreateExerciseTemplateViewModel(
                exerciseTemplateRepository = exerciseTemplateRepository,
                workoutTemplateId = workoutTemplateId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
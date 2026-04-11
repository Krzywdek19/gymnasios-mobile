package com.krzywdek19.gymnasiosmobile.domain.repository

import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseTemplate

interface ExerciseTemplateRepository {

    suspend fun getExercisesByWorkout(workoutTemplateId: String): List<ExerciseTemplate>

    suspend fun createExercise(
        workoutTemplateId: String,
        name: String,
        setsCount: Int,
        reps: String,
        orderIndex: Int,
        notes: String?
    ): ExerciseTemplate
}
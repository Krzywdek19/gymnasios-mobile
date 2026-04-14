package com.krzywdek19.gymnasiosmobile.domain.repository

import com.krzywdek19.gymnasiosmobile.data.remote.dto.UpdateExerciseTemplateRequest
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

    suspend fun updateExercise(
        id: String,
        name: String,
        setsCount: Int,
        reps: String,
        orderIndex: Int,
        notes: String?
    ): ExerciseTemplate

    suspend fun deleteExerciseById(id: String)
}
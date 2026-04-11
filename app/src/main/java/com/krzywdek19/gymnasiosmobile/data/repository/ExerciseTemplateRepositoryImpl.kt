package com.krzywdek19.gymnasiosmobile.data.repository

import com.krzywdek19.gymnasiosmobile.data.mapper.toDomain
import com.krzywdek19.gymnasiosmobile.data.remote.ExerciseTemplateApi
import com.krzywdek19.gymnasiosmobile.data.remote.dto.CreateExerciseTemplateRequest
import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseTemplate
import com.krzywdek19.gymnasiosmobile.domain.repository.ExerciseTemplateRepository

class ExerciseTemplateRepositoryImpl(
    private val api: ExerciseTemplateApi
) : ExerciseTemplateRepository {

    override suspend fun getExercisesByWorkout(workoutTemplateId: String): List<ExerciseTemplate> {
        return api.getExerciseTemplatesByWorkout(workoutTemplateId)
            .map { it.toDomain() }
    }

    override suspend fun createExercise(
        workoutTemplateId: String,
        name: String,
        setsCount: Int,
        reps: String,
        orderIndex: Int,
        notes: String?
    ): ExerciseTemplate {

        val request = CreateExerciseTemplateRequest(
            workoutTemplateId = workoutTemplateId,
            name = name,
            setsCount = setsCount,
            reps = reps,
            orderIndex = orderIndex,
            notes = notes
        )

        return api.createExerciseTemplate(workoutTemplateId, request).toDomain()
    }
}
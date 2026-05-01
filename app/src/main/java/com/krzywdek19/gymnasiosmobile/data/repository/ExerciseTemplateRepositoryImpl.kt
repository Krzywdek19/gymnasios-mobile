package com.krzywdek19.gymnasiosmobile.data.repository

import com.krzywdek19.gymnasiosmobile.data.mapper.toDomain
import com.krzywdek19.gymnasiosmobile.data.remote.ExerciseTemplateApi
import com.krzywdek19.gymnasiosmobile.data.remote.dto.CreateExerciseTemplateRequest
import com.krzywdek19.gymnasiosmobile.data.remote.dto.UpdateExerciseTemplateRequest
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
        notes: String,
        restBetweenSetsSeconds: Int,
        restAfterExerciseSeconds: Int,
        orderIndex: Int
    ): ExerciseTemplate {
        return api.createExerciseTemplate(
            workoutTemplateId = workoutTemplateId,
            request = CreateExerciseTemplateRequest(
                name = name,
                notes = notes.takeIf { it.isNotBlank() },
                setsCount = setsCount,
                reps = reps.takeIf { it.isNotBlank() },
                restBetweenSetsSeconds = restBetweenSetsSeconds,
                restAfterExerciseSeconds = restAfterExerciseSeconds,
                orderIndex = orderIndex
            )
        ).toDomain()
    }

    override suspend fun updateExercise(
        exerciseTemplateId: String,
        name: String,
        setsCount: Int,
        reps: String,
        notes: String,
        restBetweenSetsSeconds: Int,
        restAfterExerciseSeconds: Int,
        orderIndex: Int
    ): ExerciseTemplate {
        return api.updateExerciseTemplate(
            exerciseTemplateId = exerciseTemplateId,
            request = UpdateExerciseTemplateRequest(
                name = name,
                notes = notes.takeIf { it.isNotBlank() },
                orderIndex = orderIndex,
                reps = reps.takeIf { it.isNotBlank() },
                restBetweenSetsSeconds = restBetweenSetsSeconds,
                restAfterExerciseSeconds = restAfterExerciseSeconds,
                setsCount = setsCount
            )
        ).toDomain()
    }

    override suspend fun deleteExerciseById(id: String) {
        api.deleteExerciseTemplateById(id)
    }
}
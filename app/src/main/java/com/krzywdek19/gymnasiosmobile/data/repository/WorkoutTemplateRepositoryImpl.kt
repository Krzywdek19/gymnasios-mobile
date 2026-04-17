package com.krzywdek19.gymnasiosmobile.data.repository

import com.krzywdek19.gymnasiosmobile.data.mapper.toDomain
import com.krzywdek19.gymnasiosmobile.data.remote.WorkoutTemplateApi
import com.krzywdek19.gymnasiosmobile.data.remote.dto.CreateWorkoutTemplateRequest
import com.krzywdek19.gymnasiosmobile.data.remote.dto.UpdateWorkoutTemplateRequest
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutTemplate
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutTemplateRepository

class WorkoutTemplateRepositoryImpl(
    private val api: WorkoutTemplateApi
) : WorkoutTemplateRepository {

    override suspend fun createWorkoutTemplate(
        planId: String,
        name: String,
        orderIndex: Int
    ): WorkoutTemplate {
        val request = CreateWorkoutTemplateRequest(
            name = name,
            orderIndex = orderIndex
        )

        return api.createWorkoutTemplate(
            planId = planId,
            request = request
        ).toDomain()
    }

    override suspend fun getWorkoutById(workoutId: String): WorkoutTemplate {
        return api.getWorkoutTemplateById(workoutId).toDomain()
    }

    override suspend fun updateWorkoutTemplate(
        workoutId: String,
        name: String
    ): WorkoutTemplate {
        return api.updateWorkoutTemplate(
            workoutTemplateId = workoutId,
            request = UpdateWorkoutTemplateRequest(name = name)
        ).toDomain()
    }

    override suspend fun deleteWorkoutTemplateById(workoutId: String) {
        api.deleteWorkoutTemplateById(workoutId)
    }
}
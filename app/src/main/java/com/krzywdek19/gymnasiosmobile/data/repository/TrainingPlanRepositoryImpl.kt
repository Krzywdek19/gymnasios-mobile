package com.krzywdek19.gymnasiosmobile.data.repository

import com.krzywdek19.gymnasiosmobile.data.mapper.toDomain
import com.krzywdek19.gymnasiosmobile.data.remote.TrainingPlanApi
import com.krzywdek19.gymnasiosmobile.data.remote.dto.CreateTrainingPlanRequest
import com.krzywdek19.gymnasiosmobile.data.remote.dto.UpdateTrainingPlanRequest
import com.krzywdek19.gymnasiosmobile.domain.model.TrainingPlan
import com.krzywdek19.gymnasiosmobile.domain.repository.TrainingPlanRepository

class TrainingPlanRepositoryImpl(
    private val api: TrainingPlanApi
) : TrainingPlanRepository {

    override suspend fun getTrainingPlans(): List<TrainingPlan> {
        return api.getTrainingPlans()
            .map { it.toDomain() }
    }

    override suspend fun getActiveTrainingPlan(): TrainingPlan {
        return api.getActiveTrainingPlan().toDomain()
    }

    override suspend fun getTrainingPlanById(id: String): TrainingPlan {
        return api.getTrainingPlanById(id).toDomain()
    }

    override suspend fun createTrainingPlan(name: String): TrainingPlan {
        return api.createTrainingPlan(
            CreateTrainingPlanRequest(name = name)
        ).toDomain()
    }

    override suspend fun activateTrainingPlan(planId: String): TrainingPlan {
        return api.activateTrainingPlan(planId).toDomain()
    }

    override suspend fun updateTrainingPlan(planId: String, name: String): TrainingPlan {
        return api.updateTrainingPlan(
            planId = planId,
            request = UpdateTrainingPlanRequest(name = name)
        ).toDomain()
    }

    override suspend fun deleteTrainingPlan(planId: String) {
        api.deleteTrainingPlanById(planId)
    }
}
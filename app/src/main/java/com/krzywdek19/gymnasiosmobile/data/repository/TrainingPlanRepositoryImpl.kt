package com.krzywdek19.gymnasiosmobile.data.repository

import com.krzywdek19.gymnasiosmobile.core.network.ApiFactory.trainingPlanApi
import com.krzywdek19.gymnasiosmobile.data.mapper.toDomain
import com.krzywdek19.gymnasiosmobile.data.remote.TrainingPlanApi
import com.krzywdek19.gymnasiosmobile.data.remote.dto.CreateTrainingPlanRequest
import com.krzywdek19.gymnasiosmobile.data.remote.dto.UpdateTrainingPlanRequest
import com.krzywdek19.gymnasiosmobile.domain.model.TrainingPlan
import com.krzywdek19.gymnasiosmobile.domain.repository.TrainingPlanRepository

class TrainingPlanRepositoryImpl (
    private val api: TrainingPlanApi
) : TrainingPlanRepository {
    override suspend fun getTrainingPlans(): List<TrainingPlan> {
        return api.getTrainingPlans()
            .map { it.toDomain() }
    }

    override suspend fun getTrainingPlanById(id: String): TrainingPlan {
        return api.getTrainingPlanById(id).toDomain()
    }

    override suspend fun createTrainingPlan(name: String): TrainingPlan {
        val response = api.createTrainingPlan(
            CreateTrainingPlanRequest(name = name)
        )

        return response.toDomain()
    }

    override suspend fun updateTrainingPlan(planId: String, name: String): TrainingPlan {
        return trainingPlanApi.updateTrainingPlan(
            planId = planId,
            request = UpdateTrainingPlanRequest(name = name)
        ).toDomain()
    }

    override suspend fun deleteTrainingPlan(planId: String) {
        trainingPlanApi.deleteTrainingPlanById(planId)
    }
}
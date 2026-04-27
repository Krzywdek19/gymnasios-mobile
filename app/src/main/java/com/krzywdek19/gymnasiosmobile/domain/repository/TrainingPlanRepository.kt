package com.krzywdek19.gymnasiosmobile.domain.repository

import com.krzywdek19.gymnasiosmobile.domain.model.TrainingPlan

interface TrainingPlanRepository {
    suspend fun getTrainingPlans(): List<TrainingPlan>

    suspend fun getActiveTrainingPlan(): TrainingPlan

    suspend fun getTrainingPlanById(id: String): TrainingPlan

    suspend fun createTrainingPlan(name: String): TrainingPlan

    suspend fun activateTrainingPlan(planId: String): TrainingPlan

    suspend fun updateTrainingPlan(planId: String, name: String): TrainingPlan

    suspend fun deleteTrainingPlan(planId: String)
}
package com.krzywdek19.gymnasiosmobile.domain.repository

import com.krzywdek19.gymnasiosmobile.domain.model.TrainingPlan

interface TrainingPlanRepository {
    suspend fun getTrainingPlans(): List<TrainingPlan>
}
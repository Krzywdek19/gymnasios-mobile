package com.krzywdek19.gymnasiosmobile.data.remote

import com.krzywdek19.gymnasiosmobile.data.remote.dto.TrainingPlanDto
import retrofit2.http.GET

interface TrainingPlanApi {
    @GET("api/v1/training-plans")
    suspend fun getTrainingPlans(): List<TrainingPlanDto>
}
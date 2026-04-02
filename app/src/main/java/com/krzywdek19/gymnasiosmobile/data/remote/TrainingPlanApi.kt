package com.krzywdek19.gymnasiosmobile.data.remote

import com.krzywdek19.gymnasiosmobile.data.remote.dto.CreateTrainingPlanRequest
import com.krzywdek19.gymnasiosmobile.data.remote.dto.TrainingPlanDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrainingPlanApi {
    @GET("api/v1/training-plans")
    suspend fun getTrainingPlans(): List<TrainingPlanDto>

    @GET("api/v1/training-plans/{id}")
    suspend fun getTrainingPlanById(
        @Path("id") id: String
    ): TrainingPlanDto

    @POST("api/v1/training-plans")
    suspend fun createTrainingPlan(
        @Body request: CreateTrainingPlanRequest
    ): TrainingPlanDto
}
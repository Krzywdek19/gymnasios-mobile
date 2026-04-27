package com.krzywdek19.gymnasiosmobile.data.remote

import com.krzywdek19.gymnasiosmobile.data.remote.dto.CreateTrainingPlanRequest
import com.krzywdek19.gymnasiosmobile.data.remote.dto.TrainingPlanDto
import com.krzywdek19.gymnasiosmobile.data.remote.dto.UpdateTrainingPlanRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TrainingPlanApi {

    @GET("api/v1/training-plans")
    suspend fun getTrainingPlans(): List<TrainingPlanDto>

    @GET("api/v1/training-plans/{planId}")
    suspend fun getTrainingPlanById(
        @Path("planId") planId: String
    ): TrainingPlanDto

    @GET("api/v1/training-plans/active")
    suspend fun getActiveTrainingPlan(): TrainingPlanDto

    @PUT("api/v1/training-plans/{planId}/activate")
    suspend fun activateTrainingPlan(
        @Path("planId") planId: String
    ): TrainingPlanDto

    @POST("api/v1/training-plans")
    suspend fun createTrainingPlan(
        @Body request: CreateTrainingPlanRequest
    ): TrainingPlanDto

    @PUT("api/v1/training-plans/{planId}")
    suspend fun updateTrainingPlan(
        @Path("planId") planId: String,
        @Body request: UpdateTrainingPlanRequest
    ): TrainingPlanDto

    @DELETE("api/v1/training-plans/{planId}")
    suspend fun deleteTrainingPlanById(
        @Path("planId") planId: String
    )
}
package com.krzywdek19.gymnasiosmobile.data.remote

import com.krzywdek19.gymnasiosmobile.data.remote.dto.CreateWorkoutTemplateRequest
import com.krzywdek19.gymnasiosmobile.data.remote.dto.WorkoutTemplateDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface WorkoutTemplateApi {

    @POST("api/v1/training-plans/{planId}/workout-templates")
    suspend fun createWorkoutTemplate(
        @Path("planId") planId: String,
        @Body request: CreateWorkoutTemplateRequest
    ): WorkoutTemplateDto

    @DELETE("api/v1/workout-templates/{workoutId}")
    suspend fun deleteWorkoutTemplateById(
        @Path("workoutId") workoutId: String
    )
}
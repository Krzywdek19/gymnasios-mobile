package com.krzywdek19.gymnasiosmobile.data.remote

import com.krzywdek19.gymnasiosmobile.data.remote.dto.CreateExerciseTemplateRequest
import com.krzywdek19.gymnasiosmobile.data.remote.dto.ExerciseTemplateDto
import com.krzywdek19.gymnasiosmobile.data.remote.dto.UpdateExerciseTemplateRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ExerciseTemplateApi {

    @GET("api/v1/workout-templates/{workoutTemplateId}/exercise-templates")
    suspend fun getExerciseTemplatesByWorkout(
        @Path("workoutTemplateId") workoutTemplateId: String
    ): List<ExerciseTemplateDto>

    @POST("api/v1/workout-templates/{workoutTemplateId}/exercise-templates")
    suspend fun createExerciseTemplate(
        @Path("workoutTemplateId") workoutTemplateId: String,
        @Body request: CreateExerciseTemplateRequest
    ): ExerciseTemplateDto

    @PUT("api/v1/exercise-templates/{exerciseTemplateId}")
    suspend fun updateExerciseTemplate(
        @Path("exerciseTemplateId") exerciseTemplateId: String,
        @Body request: UpdateExerciseTemplateRequest
    ): ExerciseTemplateDto

    @DELETE("api/v1/exercise-templates/{exerciseTemplateId}")
    suspend fun deleteExerciseTemplateById(
        @Path("exerciseTemplateId") exerciseTemplateId: String
    )
}
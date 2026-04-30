package com.krzywdek19.gymnasiosmobile.data.remote

import com.krzywdek19.gymnasiosmobile.data.remote.dto.StartWorkoutSessionRequest
import com.krzywdek19.gymnasiosmobile.data.remote.dto.UpdateSetRequest
import com.krzywdek19.gymnasiosmobile.data.remote.dto.SetSessionDto
import com.krzywdek19.gymnasiosmobile.data.remote.dto.WorkoutSessionDto
import com.krzywdek19.gymnasiosmobile.data.remote.dto.WorkoutTemplateDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WorkoutSessionApi {

    @POST("api/v1/workout-sessions")
    suspend fun startWorkoutSession(
        @Body request: StartWorkoutSessionRequest
    ): WorkoutSessionDto

    @POST("api/v1/workout-sessions/start-next")
    suspend fun startNextWorkoutSession(): WorkoutSessionDto

    @GET("api/v1/workout-sessions/active")
    suspend fun getActiveWorkoutSession(): WorkoutSessionDto

    @GET("api/v1/workout-sessions/next-workout")
    suspend fun getNextWorkoutTemplate(): WorkoutTemplateDto

    @GET("api/v1/workout-sessions/{workoutSessionId}")
    suspend fun getWorkoutSessionById(
        @Path("workoutSessionId") workoutSessionId: String
    ): WorkoutSessionDto

    @GET("api/v1/workout-sessions")
    suspend fun getWorkoutSessions(): List<WorkoutSessionDto>

    @POST("api/v1/workout-sessions/{workoutSessionId}/finish")
    suspend fun finishWorkoutSession(
        @Path("workoutSessionId") workoutSessionId: String
    ): WorkoutSessionDto

    @PUT("api/v1/set-sessions/{setSessionId}")
    suspend fun updateSetSession(
        @Path("setSessionId") setSessionId: String,
        @Body request: UpdateSetRequest
    ): SetSessionDto

    @DELETE("api/v1/workout-sessions/{workoutSessionId}")
    suspend fun deleteWorkoutSession(
        @Path("workoutSessionId") workoutSessionId: String
    )

    @DELETE("api/v1/workout-sessions/history")
    suspend fun deleteFinishedWorkoutSessions()

    @DELETE("api/v1/workout-sessions/history/workout-templates/{workoutTemplateId}")
    suspend fun deleteFinishedWorkoutSessionsByWorkoutTemplate(
        @Path("workoutTemplateId") workoutTemplateId: String
    )
}
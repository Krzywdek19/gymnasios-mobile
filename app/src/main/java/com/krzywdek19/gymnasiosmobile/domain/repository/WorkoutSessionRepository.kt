package com.krzywdek19.gymnasiosmobile.domain.repository

import com.krzywdek19.gymnasiosmobile.domain.model.SetSession
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSession
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutTemplate

interface WorkoutSessionRepository {

    suspend fun startWorkoutSession(workoutTemplateId: String): Result<WorkoutSession>

    suspend fun startNextWorkoutSession(): Result<WorkoutSession>

    suspend fun getActiveWorkoutSession(): Result<WorkoutSession>

    suspend fun getNextWorkoutTemplate(): Result<WorkoutTemplate>

    suspend fun getWorkoutSessionById(workoutSessionId: String): Result<WorkoutSession>

    suspend fun getWorkoutSessions(): Result<List<WorkoutSession>>

    suspend fun finishWorkoutSession(workoutSessionId: String): Result<WorkoutSession>

    suspend fun updateSetSession(
        setSessionId: String,
        reps: Int,
        weight: Double,
        rir: Int?,
        completed: Boolean
    ): Result<SetSession>
    suspend fun deleteWorkoutSession(workoutSessionId: String): Result<Unit>

    suspend fun deleteFinishedWorkoutSessions(): Result<Unit>

    suspend fun deleteFinishedWorkoutSessionsByWorkoutTemplate(
        workoutTemplateId: String
    ): Result<Unit>
}
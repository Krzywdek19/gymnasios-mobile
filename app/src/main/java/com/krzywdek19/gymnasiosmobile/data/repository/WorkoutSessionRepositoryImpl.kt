package com.krzywdek19.gymnasiosmobile.data.repository

import com.krzywdek19.gymnasiosmobile.data.mapper.toDomain
import com.krzywdek19.gymnasiosmobile.data.remote.WorkoutSessionApi
import com.krzywdek19.gymnasiosmobile.data.remote.dto.StartWorkoutSessionRequest
import com.krzywdek19.gymnasiosmobile.data.remote.dto.UpdateSetRequest
import com.krzywdek19.gymnasiosmobile.domain.model.SetSession
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSession
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutTemplate
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutSessionRepository

class WorkoutSessionRepositoryImpl(
    private val workoutSessionApi: WorkoutSessionApi
) : WorkoutSessionRepository {

    override suspend fun startWorkoutSession(workoutTemplateId: String): Result<WorkoutSession> {
        return runCatching {
            workoutSessionApi
                .startWorkoutSession(StartWorkoutSessionRequest(workoutTemplateId))
                .toDomain()
        }
    }

    override suspend fun startNextWorkoutSession(): Result<WorkoutSession> {
        return runCatching {
            workoutSessionApi
                .startNextWorkoutSession()
                .toDomain()
        }
    }

    override suspend fun getActiveWorkoutSession(): Result<WorkoutSession> {
        return runCatching {
            workoutSessionApi
                .getActiveWorkoutSession()
                .toDomain()
        }
    }

    override suspend fun getNextWorkoutTemplate(): Result<WorkoutTemplate> {
        return runCatching {
            workoutSessionApi
                .getNextWorkoutTemplate()
                .toDomain()
        }
    }

    override suspend fun getWorkoutSessionById(workoutSessionId: String): Result<WorkoutSession> {
        return runCatching {
            workoutSessionApi
                .getWorkoutSessionById(workoutSessionId)
                .toDomain()
        }
    }

    override suspend fun getWorkoutSessions(): Result<List<WorkoutSession>> {
        return runCatching {
            workoutSessionApi
                .getWorkoutSessions()
                .map { it.toDomain() }
        }
    }

    override suspend fun finishWorkoutSession(workoutSessionId: String): Result<WorkoutSession> {
        return runCatching {
            workoutSessionApi
                .finishWorkoutSession(workoutSessionId)
                .toDomain()
        }
    }

    override suspend fun updateSetSession(
        setSessionId: String,
        reps: Int,
        weight: Double,
        rir: Int?,
        completed: Boolean
    ): Result<SetSession> {
        return runCatching {
            workoutSessionApi
                .updateSetSession(
                    setSessionId = setSessionId,
                    request = UpdateSetRequest(
                        reps = reps,
                        weight = weight,
                        rir = rir,
                        completed = completed
                    )
                )
                .toDomain()
        }
    }
    override suspend fun deleteWorkoutSession(workoutSessionId: String): Result<Unit> {
        return runCatching {
            workoutSessionApi.deleteWorkoutSession(workoutSessionId)
        }
    }

    override suspend fun deleteFinishedWorkoutSessions(): Result<Unit> {
        return runCatching {
            workoutSessionApi.deleteFinishedWorkoutSessions()
        }
    }

    override suspend fun deleteFinishedWorkoutSessionsByWorkoutTemplate(
        workoutTemplateId: String
    ): Result<Unit> {
        return runCatching {
            workoutSessionApi.deleteFinishedWorkoutSessionsByWorkoutTemplate(workoutTemplateId)
        }
    }
}
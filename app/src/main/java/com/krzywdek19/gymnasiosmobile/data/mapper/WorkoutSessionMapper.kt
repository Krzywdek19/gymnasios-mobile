package com.krzywdek19.gymnasiosmobile.data.mapper

import com.krzywdek19.gymnasiosmobile.data.remote.dto.ExerciseSessionDto
import com.krzywdek19.gymnasiosmobile.data.remote.dto.SetSessionDto
import com.krzywdek19.gymnasiosmobile.data.remote.dto.WorkoutSessionDto
import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseSession
import com.krzywdek19.gymnasiosmobile.domain.model.SetSession
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSession
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSessionStatus

fun WorkoutSessionDto.toDomain(): WorkoutSession {
    return WorkoutSession(
        id = id.orEmpty(),
        workoutTemplateId = workoutTemplateId.orEmpty(),
        status = status.toWorkoutSessionStatus(),
        startedAt = startedAt,
        finishedAt = finishedAt,
        exercises = exercises
            .orEmpty()
            .map { it.toDomain() }
            .sortedBy { it.orderIndex }
    )
}

private fun String?.toWorkoutSessionStatus(): WorkoutSessionStatus {
    return when (this) {
        "IN_PROGRESS" -> WorkoutSessionStatus.IN_PROGRESS
        "FINISHED" -> WorkoutSessionStatus.FINISHED
        else -> WorkoutSessionStatus.UNKNOWN
    }
}
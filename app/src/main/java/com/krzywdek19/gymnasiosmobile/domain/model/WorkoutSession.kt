package com.krzywdek19.gymnasiosmobile.domain.model

data class WorkoutSession(
    val id: String,
    val workoutTemplateId: String,
    val status: WorkoutSessionStatus,
    val startedAt: String?,
    val finishedAt: String?,
    val exercises: List<ExerciseSession>
)
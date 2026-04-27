package com.krzywdek19.gymnasiosmobile.data.remote.dto

data class WorkoutSessionDto(
    val id: String?,
    val workoutTemplateId: String?,
    val workoutTemplateName: String?,
    val status: String?,
    val startedAt: String?,
    val finishedAt: String?,
    val exercises: List<ExerciseSessionDto>?
)
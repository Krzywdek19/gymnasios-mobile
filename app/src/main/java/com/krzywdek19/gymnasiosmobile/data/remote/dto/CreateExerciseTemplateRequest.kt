package com.krzywdek19.gymnasiosmobile.data.remote.dto

data class CreateExerciseTemplateRequest (
    val workoutTemplateId: String,
    val name: String,
    val setsCount: Int,
    val reps: String,
    val orderIndex: Int,
    val notes: String?
)
package com.krzywdek19.gymnasiosmobile.data.remote.dto

data class CreateExerciseTemplateRequest(
    val name: String,
    val notes: String?,
    val setsCount: Int,
    val reps: String?,
    val restBetweenSetsSeconds: Int?,
    val restAfterExerciseSeconds: Int?,
    val orderIndex: Int
)
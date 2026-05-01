package com.krzywdek19.gymnasiosmobile.data.remote.dto

data class UpdateExerciseTemplateRequest(
    val name: String,
    val notes: String?,
    val orderIndex: Int,
    val reps: String?,
    val restBetweenSetsSeconds: Int?,
    val restAfterExerciseSeconds: Int?,
    val setsCount: Int
)
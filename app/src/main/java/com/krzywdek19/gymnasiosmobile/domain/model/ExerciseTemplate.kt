package com.krzywdek19.gymnasiosmobile.domain.model

data class ExerciseTemplate(
    val id: String,
    val name: String,
    val notes: String,
    val setsCount: Int,
    val reps: String,
    val restBetweenSetsSeconds: Int,
    val restAfterExerciseSeconds: Int,
    val orderIndex: Int
)
package com.krzywdek19.gymnasiosmobile.domain.model

data class ExerciseTemplate(
    val id: String,
    val name: String,
    val setsCount: Int,
    val reps: String,
    val orderIndex: Int,
    val notes: String?
)
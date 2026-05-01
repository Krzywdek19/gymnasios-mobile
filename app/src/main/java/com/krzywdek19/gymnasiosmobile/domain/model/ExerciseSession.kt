package com.krzywdek19.gymnasiosmobile.domain.model

data class ExerciseSession(
    val id: String,
    val name: String,
    val orderIndex: Int,
    val setsCount: Int,
    val restBetweenSetsSeconds: Int,
    val restAfterExerciseSeconds: Int,
    val sets: List<SetSession>
)
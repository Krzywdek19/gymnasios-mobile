package com.krzywdek19.gymnasiosmobile.data.remote.dto

data class ExerciseSessionDto(
    val id: String?,
    val name: String?,
    val orderIndex: Int?,
    val setsCount: Int?,
    val restBetweenSetsSeconds: Int?,
    val restAfterExerciseSeconds: Int?,
    val sets: List<SetSessionDto>?
)
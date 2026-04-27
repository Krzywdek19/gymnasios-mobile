package com.krzywdek19.gymnasiosmobile.domain.model

data class SetSession(
    val id: String,
    val orderIndex: Int,
    val reps: Int?,
    val weight: Double?,
    val rir: Int?,
    val completed: Boolean
)
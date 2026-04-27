package com.krzywdek19.gymnasiosmobile.data.remote.dto

data class SetSessionDto(
    val id: String?,
    val orderIndex: Int?,
    val reps: Int?,
    val weight: Double?,
    val rir: Int?,
    val completed: Boolean?
)
package com.krzywdek19.gymnasiosmobile.data.remote.dto

data class UpdateSetRequest(
    val reps: Int,
    val weight: Double,
    val rir: Int?,
    val completed: Boolean
)
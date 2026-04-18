package com.krzywdek19.gymnasiosmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateWorkoutTemplateRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("order")
    val order: Int
)
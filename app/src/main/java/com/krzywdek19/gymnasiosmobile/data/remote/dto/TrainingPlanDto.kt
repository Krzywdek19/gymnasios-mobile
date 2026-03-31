package com.krzywdek19.gymnasiosmobile.data.remote.dto;

data class TrainingPlanDto (
    val id: String,
    val userEmail: String,
    val name: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)
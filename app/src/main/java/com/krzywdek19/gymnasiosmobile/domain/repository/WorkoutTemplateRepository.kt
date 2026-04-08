package com.krzywdek19.gymnasiosmobile.domain.repository;

import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutTemplate

interface WorkoutTemplateRepository {
    suspend fun createWorkoutTemplate(
        planId: String,
        name: String,
        orderIndex: Int
    ): WorkoutTemplate
}

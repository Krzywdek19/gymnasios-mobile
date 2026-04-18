package com.krzywdek19.gymnasiosmobile.domain.repository;

import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutTemplate

interface WorkoutTemplateRepository {
    suspend fun createWorkoutTemplate(
        planId: String,
        name: String,
        orderIndex: Int
    ): WorkoutTemplate

    suspend fun getWorkoutById(workoutId: String): WorkoutTemplate
    suspend fun updateWorkoutTemplate(
        workoutId: String,
        name: String,
        order: Int
    ): WorkoutTemplate
    suspend fun deleteWorkoutTemplateById(workoutId: String)
}

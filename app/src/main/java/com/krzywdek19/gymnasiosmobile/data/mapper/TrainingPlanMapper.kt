package com.krzywdek19.gymnasiosmobile.data.mapper

import com.krzywdek19.gymnasiosmobile.data.remote.dto.TrainingPlanDto
import com.krzywdek19.gymnasiosmobile.domain.model.TrainingPlan

fun TrainingPlanDto.toDomain(): TrainingPlan {
    return TrainingPlan(
        id = id.orEmpty(),
        name = name.orEmpty(),
        status = status.orEmpty(),
        workouts = workouts?.map { it.toDomain() } ?: emptyList()
    )
}
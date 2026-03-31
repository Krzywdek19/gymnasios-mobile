package com.krzywdek19.gymnasiosmobile.data.mapper

import com.krzywdek19.gymnasiosmobile.data.remote.dto.TrainingPlanDto
import com.krzywdek19.gymnasiosmobile.domain.model.TrainingPlan

fun TrainingPlanDto.toDomain(): TrainingPlan {
    return TrainingPlan(
        id = id,
        name = name,
        status = status
    )
}
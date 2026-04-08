package com.krzywdek19.gymnasiosmobile.data.mapper

import com.krzywdek19.gymnasiosmobile.data.remote.dto.WorkoutTemplateDto
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutTemplate

fun WorkoutTemplateDto.toDomain(): WorkoutTemplate {
    return WorkoutTemplate(
        id = id.orEmpty(),
        name = name.orEmpty(),
        orderIndex = orderIndex ?: 0
    )
}

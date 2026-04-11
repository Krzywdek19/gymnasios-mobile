package com.krzywdek19.gymnasiosmobile.data.mapper

import com.krzywdek19.gymnasiosmobile.data.remote.dto.ExerciseTemplateDto
import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseTemplate
fun ExerciseTemplateDto.toDomain(): ExerciseTemplate {
    return ExerciseTemplate(
        id = id,
        name = name,
        setsCount = setsCount,
        reps = reps,
        orderIndex = orderIndex,
        notes = notes
    )
}
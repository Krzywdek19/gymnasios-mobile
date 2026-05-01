package com.krzywdek19.gymnasiosmobile.data.mapper

import com.krzywdek19.gymnasiosmobile.data.remote.dto.ExerciseTemplateDto
import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseTemplate

fun ExerciseTemplateDto.toDomain(): ExerciseTemplate {
    return ExerciseTemplate(
        id = id.orEmpty(),
        name = name.orEmpty(),
        notes = notes.orEmpty(),
        setsCount = setsCount ?: 0,
        reps = reps.orEmpty(),
        restBetweenSetsSeconds = restBetweenSetsSeconds ?: 120,
        restAfterExerciseSeconds = restAfterExerciseSeconds ?: 180,
        orderIndex = orderIndex ?: 0
    )
}
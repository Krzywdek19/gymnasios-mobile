package com.krzywdek19.gymnasiosmobile.data.mapper

import com.krzywdek19.gymnasiosmobile.data.remote.dto.ExerciseSessionDto
import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseSession

fun ExerciseSessionDto.toDomain(): ExerciseSession {
    return ExerciseSession(
        id = id.orEmpty(),
        name = name.orEmpty(),
        orderIndex = orderIndex ?: 0,
        setsCount = setsCount ?: 0,
        sets = sets
            .orEmpty()
            .map { it.toDomain() }
            .sortedBy { it.orderIndex }
    )
}
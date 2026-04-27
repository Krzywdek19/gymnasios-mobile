package com.krzywdek19.gymnasiosmobile.data.mapper

import com.krzywdek19.gymnasiosmobile.data.remote.dto.SetSessionDto
import com.krzywdek19.gymnasiosmobile.domain.model.SetSession

fun SetSessionDto.toDomain(): SetSession {
    return SetSession(
        id = id.orEmpty(),
        orderIndex = orderIndex ?: 0,
        reps = reps,
        weight = weight,
        rir = rir,
        completed = completed ?: false
    )
}
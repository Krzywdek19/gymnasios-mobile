package com.krzywdek19.gymnasiosmobile.data.remote.auth

data class TokenResponseDto(
    val tokenType: String,
    val accessToken: String,
    val expiresIn: Long,
    val refreshToken: String
)
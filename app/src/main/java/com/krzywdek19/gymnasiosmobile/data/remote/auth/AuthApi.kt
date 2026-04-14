package com.krzywdek19.gymnasiosmobile.data.remote.auth

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): UserResponseDto

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): TokenResponseDto
}
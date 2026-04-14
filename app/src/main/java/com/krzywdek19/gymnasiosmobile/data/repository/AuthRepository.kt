package com.krzywdek19.gymnasiosmobile.data.repository

import com.krzywdek19.gymnasiosmobile.data.local.TokenStorage
import com.krzywdek19.gymnasiosmobile.data.remote.auth.AuthApi
import com.krzywdek19.gymnasiosmobile.data.remote.auth.LoginRequestDto
import com.krzywdek19.gymnasiosmobile.data.remote.auth.RegisterRequestDto

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) {

    suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            authApi.register(RegisterRequestDto(email, password))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = authApi.login(LoginRequestDto(email, password))
            tokenStorage.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                expiresIn = response.expiresIn
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean = tokenStorage.isLoggedIn()

    fun logout() {
        tokenStorage.clear()
    }
}
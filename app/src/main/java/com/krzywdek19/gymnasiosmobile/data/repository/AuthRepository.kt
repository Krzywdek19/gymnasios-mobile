package com.krzywdek19.gymnasiosmobile.data.repository

import com.krzywdek19.gymnasiosmobile.core.network.SessionManager
import com.krzywdek19.gymnasiosmobile.data.remote.auth.AuthApi
import com.krzywdek19.gymnasiosmobile.data.remote.auth.LoginRequestDto
import com.krzywdek19.gymnasiosmobile.data.remote.auth.RefreshTokenRequestDto
import com.krzywdek19.gymnasiosmobile.data.remote.auth.RegisterRequestDto

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager
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

            sessionManager.onLogin(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                expiresIn = response.expiresIn
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshToken(): Result<String> {
        val refreshToken = tokenStorage.getRefreshToken()
            ?: return Result.failure(IllegalStateException("Brak refresh tokenu"))

        return try {
            val response = authApi.refreshToken(
                RefreshTokenRequestDto(refreshToken)
            )

            sessionManager.onLogin(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                expiresIn = response.expiresIn
            )

            Result.success(response.accessToken)
        } catch (e: Exception) {
            sessionManager.logout()
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn.value

    fun logout() {
        sessionManager.logout()
    }
}
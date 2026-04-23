package com.krzywdek19.gymnasiosmobile.core.network

import com.krzywdek19.gymnasiosmobile.data.remote.auth.AuthApi
import com.krzywdek19.gymnasiosmobile.data.remote.auth.RefreshTokenRequestDto
import com.krzywdek19.gymnasiosmobile.data.repository.TokenStorage
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit

class TokenAuthenticator(
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager,
    private val plainRetrofit: Retrofit
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            sessionManager.logout()
            return null
        }

        val refreshToken = tokenStorage.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            sessionManager.logout()
            return null
        }

        val authApi = plainRetrofit.create(AuthApi::class.java)

        return try {
            val refreshResponse = authApi.refreshTokenCall(
                RefreshTokenRequestDto(refreshToken)
            ).execute()

            if (!refreshResponse.isSuccessful) {
                sessionManager.logout()
                return null
            }

            val body = refreshResponse.body()
            if (body == null) {
                sessionManager.logout()
                return null
            }

            sessionManager.onLogin(
                accessToken = body.accessToken,
                refreshToken = body.refreshToken,
                expiresIn = body.expiresIn
            )

            response.request.newBuilder()
                .header("Authorization", "Bearer ${body.accessToken}")
                .build()
        } catch (e: Exception) {
            sessionManager.logout()
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var current = response.priorResponse
        while (current != null) {
            result++
            current = current.priorResponse
        }
        return result
    }
}
package com.krzywdek19.gymnasiosmobile.core.network

import com.krzywdek19.gymnasiosmobile.data.local.TokenStorage
import com.krzywdek19.gymnasiosmobile.data.remote.auth.AuthApi
import com.krzywdek19.gymnasiosmobile.data.remote.auth.RefreshTokenRequestDto
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit

class TokenAuthenticator(
    private val tokenStorage: TokenStorage,
    private val plainRetrofit: Retrofit
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        val refreshToken = tokenStorage.getRefreshToken() ?: return null
        val authApi = plainRetrofit.create(AuthApi::class.java)

        val refreshCall = authApi.refreshTokenCall(
            RefreshTokenRequestDto(refreshToken)
        )

        val refreshResponse = refreshCall.execute()

        if (!refreshResponse.isSuccessful) {
            tokenStorage.clear()
            return null
        }

        val body = refreshResponse.body() ?: return null

        tokenStorage.saveTokens(
            accessToken = body.accessToken,
            refreshToken = body.refreshToken,
            expiresIn = body.expiresIn
        )

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${body.accessToken}")
            .build()
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
package com.krzywdek19.gymnasiosmobile.core.network

import com.krzywdek19.gymnasiosmobile.data.local.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (
    private val tokenStorage: TokenStorage
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val accessToken = tokenStorage.getAccessToken()

        if(accessToken.isNullOrBlank()) {
            return chain.proceed(originRequest)
        }

        val newRequest = originRequest.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(newRequest)
    }
}
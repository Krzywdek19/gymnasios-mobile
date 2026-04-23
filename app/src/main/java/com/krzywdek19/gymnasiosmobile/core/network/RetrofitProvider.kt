package com.krzywdek19.gymnasiosmobile.core.network

import com.krzywdek19.gymnasiosmobile.data.repository.TokenStorage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    private lateinit var tokenStorage: TokenStorage
    private lateinit var sessionManager: SessionManager

    fun init(
        tokenStorage: TokenStorage,
        sessionManager: SessionManager
    ) {
        this.tokenStorage = tokenStorage
        this.sessionManager = sessionManager
    }

    private val plainRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStorage))
            .authenticator(
                TokenAuthenticator(
                    tokenStorage = tokenStorage,
                    sessionManager = sessionManager,
                    plainRetrofit = plainRetrofit
                )
            )
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
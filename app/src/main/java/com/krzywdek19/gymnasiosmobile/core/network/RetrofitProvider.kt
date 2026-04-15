package com.krzywdek19.gymnasiosmobile.core.network

import com.krzywdek19.gymnasiosmobile.data.local.TokenStorage
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    private lateinit var tokenStorage: TokenStorage

    fun init(tokenStorage: TokenStorage) {
        this.tokenStorage = tokenStorage
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
            .authenticator(TokenAuthenticator(tokenStorage, plainRetrofit))
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
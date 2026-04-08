package com.krzywdek19.gymnasiosmobile.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val trainingPlanApi: TrainingPlanApi by lazy {
        retrofit.create(TrainingPlanApi::class.java)
    }

    val workoutTemplateApi: WorkoutTemplateApi by lazy {
        retrofit.create(WorkoutTemplateApi::class.java)
    }
}
package com.krzywdek19.gymnasiosmobile.core.network

import com.krzywdek19.gymnasiosmobile.data.remote.ExerciseTemplateApi
import com.krzywdek19.gymnasiosmobile.data.remote.TrainingPlanApi
import com.krzywdek19.gymnasiosmobile.data.remote.WorkoutSessionApi
import com.krzywdek19.gymnasiosmobile.data.remote.WorkoutTemplateApi
import com.krzywdek19.gymnasiosmobile.data.remote.auth.AuthApi

object ApiFactory {

    val trainingPlanApi: TrainingPlanApi by lazy {
        RetrofitProvider.retrofit.create(TrainingPlanApi::class.java)
    }

    val workoutTemplateApi: WorkoutTemplateApi by lazy {
        RetrofitProvider.retrofit.create(WorkoutTemplateApi::class.java)
    }

    val exerciseTemplateApi: ExerciseTemplateApi by lazy {
        RetrofitProvider.retrofit.create(ExerciseTemplateApi::class.java)
    }

    val workoutSessionApi: WorkoutSessionApi by lazy {
        RetrofitProvider.retrofit.create(WorkoutSessionApi::class.java)
    }

    val authApi: AuthApi by lazy {
        RetrofitProvider.retrofit.create(AuthApi::class.java)
    }
}
package com.krzywdek19.gymnasiosmobile.core.network

import com.krzywdek19.gymnasiosmobile.data.remote.ExerciseTemplateApi
import com.krzywdek19.gymnasiosmobile.data.remote.TrainingPlanApi
import com.krzywdek19.gymnasiosmobile.data.remote.WorkoutTemplateApi

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
}
package com.krzywdek19.gymnasiosmobile.di

import com.krzywdek19.gymnasiosmobile.core.network.ApiFactory
import com.krzywdek19.gymnasiosmobile.data.remote.ExerciseTemplateApi
import com.krzywdek19.gymnasiosmobile.data.repository.ExerciseTemplateRepositoryImpl
import com.krzywdek19.gymnasiosmobile.data.repository.TrainingPlanRepositoryImpl
import com.krzywdek19.gymnasiosmobile.data.repository.WorkoutTemplateRepositoryImpl
import com.krzywdek19.gymnasiosmobile.domain.repository.ExerciseTemplateRepository
import com.krzywdek19.gymnasiosmobile.domain.repository.TrainingPlanRepository
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutTemplateRepository

object AppContainer {
    private val trainingPlanApi = ApiFactory.trainingPlanApi
    private val workoutTemplateApi = ApiFactory.workoutTemplateApi
    private val ExerciseTemplateApi = ApiFactory.exerciseTemplateApi

    val trainingPlanRepository: TrainingPlanRepository =
        TrainingPlanRepositoryImpl(trainingPlanApi)

    val workoutTemplateRepository: WorkoutTemplateRepository =
        WorkoutTemplateRepositoryImpl(workoutTemplateApi)

    val exerciseTemplateRepository: ExerciseTemplateRepository =
        ExerciseTemplateRepositoryImpl(ExerciseTemplateApi)
}
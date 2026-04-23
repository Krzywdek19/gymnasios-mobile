package com.krzywdek19.gymnasiosmobile.di

import android.content.Context
import com.krzywdek19.gymnasiosmobile.core.network.ApiFactory
import com.krzywdek19.gymnasiosmobile.core.network.SessionManager
import com.krzywdek19.gymnasiosmobile.data.repository.AuthRepository
import com.krzywdek19.gymnasiosmobile.data.repository.ExerciseTemplateRepositoryImpl
import com.krzywdek19.gymnasiosmobile.data.repository.TokenStorage
import com.krzywdek19.gymnasiosmobile.data.repository.TrainingPlanRepositoryImpl
import com.krzywdek19.gymnasiosmobile.data.repository.WorkoutTemplateRepositoryImpl
import com.krzywdek19.gymnasiosmobile.domain.repository.ExerciseTemplateRepository
import com.krzywdek19.gymnasiosmobile.domain.repository.TrainingPlanRepository
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutTemplateRepository

object AppContainer {

    private lateinit var tokenStorage: TokenStorage
    lateinit var sessionManager: SessionManager
        private set

    fun init(context: Context) {
        if (::sessionManager.isInitialized) return

        tokenStorage = TokenStorage(context.applicationContext)
        sessionManager = SessionManager(tokenStorage)
    }

    fun getTokenStorage(): TokenStorage = tokenStorage

    val authRepository: AuthRepository by lazy {
        AuthRepository(
            authApi = ApiFactory.authApi,
            tokenStorage = tokenStorage,
            sessionManager = sessionManager
        )
    }

    val trainingPlanRepository: TrainingPlanRepository by lazy {
        TrainingPlanRepositoryImpl(ApiFactory.trainingPlanApi)
    }

    val workoutTemplateRepository: WorkoutTemplateRepository by lazy {
        WorkoutTemplateRepositoryImpl(ApiFactory.workoutTemplateApi)
    }

    val exerciseTemplateRepository: ExerciseTemplateRepository by lazy {
        ExerciseTemplateRepositoryImpl(ApiFactory.exerciseTemplateApi)
    }
}
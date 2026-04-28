package com.krzywdek19.gymnasiosmobile.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krzywdek19.gymnasiosmobile.core.network.ApiFactory
import com.krzywdek19.gymnasiosmobile.data.repository.TrainingPlanRepositoryImpl
import com.krzywdek19.gymnasiosmobile.data.repository.WorkoutSessionRepositoryImpl

class DashboardViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val trainingPlanRepository = TrainingPlanRepositoryImpl(
            api = ApiFactory.trainingPlanApi
        )

        val workoutSessionRepository = WorkoutSessionRepositoryImpl(
            workoutSessionApi = ApiFactory.workoutSessionApi
        )

        return DashboardViewModel(
            trainingPlanRepository = trainingPlanRepository,
            workoutSessionRepository = workoutSessionRepository
        ) as T
    }
}
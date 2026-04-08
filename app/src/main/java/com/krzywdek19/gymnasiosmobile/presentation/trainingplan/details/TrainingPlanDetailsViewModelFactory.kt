package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krzywdek19.gymnasiosmobile.data.remote.RetrofitClient
import com.krzywdek19.gymnasiosmobile.data.repository.TrainingPlanRepositoryImpl
import com.krzywdek19.gymnasiosmobile.data.repository.WorkoutTemplateRepositoryImpl

class TrainingPlanDetailsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val trainingPlanApi = RetrofitClient.trainingPlanApi
        val workoutTemplateApi = RetrofitClient.workoutTemplateApi

        val trainingPlanRepository = TrainingPlanRepositoryImpl(trainingPlanApi)
        val workoutTemplateRepository = WorkoutTemplateRepositoryImpl(workoutTemplateApi)

        return TrainingPlanDetailsViewModel(
            repository = trainingPlanRepository,
            workoutTemplateRepository = workoutTemplateRepository
        ) as T
    }
}
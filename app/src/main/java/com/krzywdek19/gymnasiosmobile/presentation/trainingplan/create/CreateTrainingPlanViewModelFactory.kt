package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krzywdek19.gymnasiosmobile.data.remote.RetrofitClient
import com.krzywdek19.gymnasiosmobile.data.repository.TrainingPlanRepositoryImpl

class CreateTrainingPlanViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitClient.trainingPlanApi
        val repository = TrainingPlanRepositoryImpl(api)
        return CreateTrainingPlanViewModel(repository) as T
    }
}
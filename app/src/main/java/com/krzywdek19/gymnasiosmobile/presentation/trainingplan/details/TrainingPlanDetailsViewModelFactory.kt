package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krzywdek19.gymnasiosmobile.data.remote.RetrofitClient
import com.krzywdek19.gymnasiosmobile.data.repository.TrainingPlanRepositoryImpl

class TrainingPlanDetailsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = RetrofitClient.trainingPlanApi
        val repository = TrainingPlanRepositoryImpl(api)
        return TrainingPlanDetailsViewModel(repository) as T
    }
}
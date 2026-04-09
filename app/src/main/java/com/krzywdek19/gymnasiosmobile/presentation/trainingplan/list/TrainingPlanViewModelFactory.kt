package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krzywdek19.gymnasiosmobile.core.network.ApiFactory
import com.krzywdek19.gymnasiosmobile.core.network.RetrofitProvider
import com.krzywdek19.gymnasiosmobile.data.repository.TrainingPlanRepositoryImpl

class TrainingPlanViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = ApiFactory.trainingPlanApi
        val repository = TrainingPlanRepositoryImpl(api)
        return TrainingPlanViewModel(repository) as T
    }
}
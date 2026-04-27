package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krzywdek19.gymnasiosmobile.core.network.ApiFactory
import com.krzywdek19.gymnasiosmobile.data.repository.WorkoutSessionRepositoryImpl

class WorkoutSessionHistoryViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val workoutSessionRepository = WorkoutSessionRepositoryImpl(
            workoutSessionApi = ApiFactory.workoutSessionApi
        )

        return WorkoutSessionHistoryViewModel(
            workoutSessionRepository = workoutSessionRepository
        ) as T
    }
}
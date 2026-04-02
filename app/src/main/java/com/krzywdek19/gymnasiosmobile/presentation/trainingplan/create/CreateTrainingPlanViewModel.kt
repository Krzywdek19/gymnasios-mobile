package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.domain.repository.TrainingPlanRepository
import kotlinx.coroutines.launch

class CreateTrainingPlanViewModel(
    private val repository: TrainingPlanRepository
) : ViewModel() {
    var name by mutableStateOf("")
        private set

    fun onNameChange(newValue: String) {
        name = newValue
    }

    fun createPlan(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.createTrainingPlan(name)
                onSuccess()
            } catch (e: Exception) {

            }
        }
    }
}
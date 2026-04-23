package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.create

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.repository.TrainingPlanRepository
import kotlinx.coroutines.launch

class CreateTrainingPlanViewModel(
    private val repository: TrainingPlanRepository
) : ViewModel() {

    var name by mutableStateOf("")
        private set

    var isSaving by mutableStateOf(false)
        private set

    @get:StringRes
    var errorMessageRes by mutableStateOf<Int?>(null)
        private set

    var savedSuccessfully by mutableStateOf(false)
        private set

    fun onNameChange(newValue: String) {
        name = newValue
        errorMessageRes = null
    }

    fun createPlan() {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            errorMessageRes = R.string.error_training_plan_name_required
            return
        }

        viewModelScope.launch {
            isSaving = true
            errorMessageRes = null

            try {
                repository.createTrainingPlan(trimmedName)
                savedSuccessfully = true
            } catch (_: Exception) {
                errorMessageRes = R.string.error_training_plan_create_failed
            } finally {
                isSaving = false
            }
        }
    }

    fun clearSuccessFlag() {
        savedSuccessfully = false
    }
}
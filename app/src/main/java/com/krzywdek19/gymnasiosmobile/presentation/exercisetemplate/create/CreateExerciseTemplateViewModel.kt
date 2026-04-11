package com.krzywdek19.gymnasiosmobile.presentation.exercisetemplate.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.repository.ExerciseTemplateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateExerciseTemplateUiState(
    val name: String = "",
    val setsCount: String = "",
    val reps: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessageRes: Int? = null,
    val savedSuccessfully: Boolean = false
)

class CreateExerciseTemplateViewModel(
    private val exerciseTemplateRepository: ExerciseTemplateRepository,
    private val workoutTemplateId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateExerciseTemplateUiState())
    val uiState: StateFlow<CreateExerciseTemplateUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            name = value,
            errorMessageRes = null
        )
    }

    fun onSetsCountChange(value: String) {
        _uiState.value = _uiState.value.copy(
            setsCount = value,
            errorMessageRes = null
        )
    }

    fun onRepsChange(value: String) {
        _uiState.value = _uiState.value.copy(
            reps = value,
            errorMessageRes = null
        )
    }

    fun onNotesChange(value: String) {
        _uiState.value = _uiState.value.copy(
            notes = value,
            errorMessageRes = null
        )
    }

    fun saveExercise(orderIndex: Int) {
        val current = _uiState.value
        val sets = current.setsCount.toIntOrNull()

        if (current.name.isBlank()) {
            _uiState.value = current.copy(errorMessageRes = R.string.error_exercise_name_required)
            return
        }

        if (sets == null || sets <= 0) {
            _uiState.value = current.copy(errorMessageRes = R.string.error_sets_count_invalid)
            return
        }

        if (current.reps.isBlank()) {
            _uiState.value = current.copy(errorMessageRes = R.string.error_reps_required)
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(
                isSaving = true,
                errorMessageRes = null
            )

            try {
                exerciseTemplateRepository.createExercise(
                    workoutTemplateId = workoutTemplateId,
                    name = current.name.trim(),
                    setsCount = sets,
                    reps = current.reps.trim(),
                    orderIndex = orderIndex,
                    notes = current.notes.trim().ifBlank { null }
                )

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    savedSuccessfully = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessageRes = R.string.error_saving_exercise
                )
            }
        }
    }

    fun clearSuccessFlag() {
        _uiState.value = _uiState.value.copy(savedSuccessfully = false)
    }
}
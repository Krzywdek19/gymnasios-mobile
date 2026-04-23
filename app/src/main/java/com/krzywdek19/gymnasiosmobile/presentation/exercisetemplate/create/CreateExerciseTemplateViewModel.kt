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
        updateForm(name = value)
    }

    fun onSetsCountChange(value: String) {
        updateForm(setsCount = value)
    }

    fun onRepsChange(value: String) {
        updateForm(reps = value)
    }

    fun onNotesChange(value: String) {
        updateForm(notes = value)
    }

    fun saveExercise(orderIndex: Int) {
        val currentState = _uiState.value
        val validatedInput = validateInput(currentState) ?: return

        viewModelScope.launch {
            _uiState.value = currentState.copy(
                isSaving = true,
                errorMessageRes = null
            )

            try {
                exerciseTemplateRepository.createExercise(
                    workoutTemplateId = workoutTemplateId,
                    name = validatedInput.name,
                    setsCount = validatedInput.setsCount,
                    reps = validatedInput.reps,
                    orderIndex = orderIndex,
                    notes = validatedInput.notes
                )

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    savedSuccessfully = true,
                    errorMessageRes = null
                )
            } catch (_: Exception) {
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

    private fun updateForm(
        name: String = _uiState.value.name,
        setsCount: String = _uiState.value.setsCount,
        reps: String = _uiState.value.reps,
        notes: String = _uiState.value.notes
    ) {
        _uiState.value = _uiState.value.copy(
            name = name,
            setsCount = setsCount,
            reps = reps,
            notes = notes,
            errorMessageRes = null
        )
    }

    private fun validateInput(state: CreateExerciseTemplateUiState): ValidatedExerciseInput? {
        val name = state.name.trim()
        val reps = state.reps.trim()
        val notes = state.notes.trim().ifBlank { null }
        val setsCount = state.setsCount.trim().toIntOrNull()

        when {
            name.isBlank() -> {
                _uiState.value = state.copy(errorMessageRes = R.string.error_exercise_name_required)
                return null
            }

            setsCount == null || setsCount <= 0 -> {
                _uiState.value = state.copy(errorMessageRes = R.string.error_sets_count_invalid)
                return null
            }

            reps.isBlank() -> {
                _uiState.value = state.copy(errorMessageRes = R.string.error_reps_required)
                return null
            }
        }

        return ValidatedExerciseInput(
            name = name,
            setsCount = setsCount,
            reps = reps,
            notes = notes
        )
    }

    private data class ValidatedExerciseInput(
        val name: String,
        val setsCount: Int,
        val reps: String,
        val notes: String?
    )
}
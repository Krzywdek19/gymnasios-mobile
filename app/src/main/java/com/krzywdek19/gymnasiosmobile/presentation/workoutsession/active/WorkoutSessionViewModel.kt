package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseSession
import com.krzywdek19.gymnasiosmobile.domain.model.SetSession
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSession
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutSessionViewModel(
    private val workoutSessionRepository: WorkoutSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutSessionUiState>(
        WorkoutSessionUiState.Loading
    )
    val uiState: StateFlow<WorkoutSessionUiState> = _uiState.asStateFlow()

    fun loadWorkoutSession(workoutSessionId: String) {
        viewModelScope.launch {
            _uiState.value = WorkoutSessionUiState.Loading

            workoutSessionRepository
                .getWorkoutSessionById(workoutSessionId)
                .onSuccess { session ->
                    _uiState.value = WorkoutSessionUiState.Success(
                        session = session
                    )
                }
                .onFailure {
                    _uiState.value = WorkoutSessionUiState.Error(
                        messageRes = R.string.error_workout_session_load_failed
                    )
                }
        }
    }

    fun saveSet(
        setSessionId: String,
        repsText: String,
        weightText: String,
        rirText: String
    ) {
        val currentState = currentSuccessOrNull() ?: return

        val reps = repsText.trim().toIntOrNull()
        val weight = weightText.trim().replace(",", ".").toDoubleOrNull()
        val rir = rirText.trim().takeIf { it.isNotBlank() }?.toIntOrNull()

        if (reps == null || reps <= 0 || weight == null || weight < 0) {
            _uiState.value = currentState.copy(
                actionErrorMessageRes = R.string.error_invalid_set_values
            )
            return
        }

        _uiState.value = currentState.copy(
            savingSetIds = currentState.savingSetIds + setSessionId,
            actionErrorMessageRes = null
        )

        viewModelScope.launch {
            workoutSessionRepository
                .updateSetSession(
                    setSessionId = setSessionId,
                    reps = reps,
                    weight = weight,
                    rir = rir,
                    completed = true
                )
                .onSuccess { updatedSet ->
                    replaceSetInCurrentSession(updatedSet)
                }
                .onFailure {
                    val latestState = currentSuccessOrNull() ?: return@onFailure
                    _uiState.value = latestState.copy(
                        savingSetIds = latestState.savingSetIds - setSessionId,
                        actionErrorMessageRes = R.string.error_save_set_failed
                    )
                }
        }
    }

    fun finishWorkoutSession(
        onFinished: () -> Unit
    ) {
        val currentState = currentSuccessOrNull() ?: return

        _uiState.value = currentState.copy(
            isFinishingSession = true,
            actionErrorMessageRes = null
        )

        viewModelScope.launch {
            workoutSessionRepository
                .finishWorkoutSession(currentState.session.id)
                .onSuccess {
                    onFinished()
                }
                .onFailure {
                    val latestState = currentSuccessOrNull() ?: return@onFailure
                    _uiState.value = latestState.copy(
                        isFinishingSession = false,
                        actionErrorMessageRes = R.string.error_finish_workout_session_failed
                    )
                }
        }
    }

    private fun replaceSetInCurrentSession(updatedSet: SetSession) {
        val currentState = currentSuccessOrNull() ?: return

        val updatedSession = currentState.session.copy(
            exercises = currentState.session.exercises.map { exercise ->
                exercise.copy(
                    sets = exercise.sets.map { set ->
                        if (set.id == updatedSet.id) updatedSet else set
                    }
                )
            }
        )

        _uiState.value = currentState.copy(
            session = updatedSession,
            savingSetIds = currentState.savingSetIds - updatedSet.id,
            actionErrorMessageRes = null
        )
    }

    private fun currentSuccessOrNull(): WorkoutSessionUiState.Success? {
        return _uiState.value as? WorkoutSessionUiState.Success
    }
}
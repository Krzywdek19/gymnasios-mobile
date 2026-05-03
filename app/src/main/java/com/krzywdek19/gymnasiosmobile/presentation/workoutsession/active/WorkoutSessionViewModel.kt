package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseSession
import com.krzywdek19.gymnasiosmobile.domain.model.SetSession
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSession
import com.krzywdek19.gymnasiosmobile.domain.repository.WorkoutSessionRepository
import com.krzywdek19.gymnasiosmobile.presentation.workoutsession.active.state.GuidedWorkoutPhase
import com.krzywdek19.gymnasiosmobile.presentation.workoutsession.active.state.GuidedWorkoutState
import com.krzywdek19.gymnasiosmobile.presentation.workoutsession.active.state.WorkoutExecutionMode
import com.krzywdek19.gymnasiosmobile.presentation.workoutsession.active.state.WorkoutSessionUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private var restTimerJob: Job? = null

    fun loadWorkoutSession(workoutSessionId: String) {
        viewModelScope.launch {
            restTimerJob?.cancel()
            _uiState.value = WorkoutSessionUiState.Loading

            workoutSessionRepository
                .getWorkoutSessionById(workoutSessionId)
                .onSuccess { session ->
                    _uiState.value = WorkoutSessionUiState.Success(
                        session = session,
                        guidedWorkoutState = createInitialGuidedState(session)
                    )
                }
                .onFailure {
                    _uiState.value = WorkoutSessionUiState.Error(
                        messageRes = R.string.error_workout_session_load_failed
                    )
                }
        }
    }

    fun switchToListMode() {
        restTimerJob?.cancel()

        val currentState = currentSuccessOrNull() ?: return

        _uiState.value = currentState.copy(
            displayMode = WorkoutExecutionMode.LIST,
            actionErrorMessageRes = null
        )
    }

    fun switchToGuidedMode() {
        val currentState = currentSuccessOrNull() ?: return

        val guidedState = if (currentState.guidedWorkoutState.phase == GuidedWorkoutPhase.FINISHED) {
            currentState.guidedWorkoutState
        } else {
            createInitialGuidedState(currentState.session)
        }

        _uiState.value = currentState.copy(
            displayMode = WorkoutExecutionMode.GUIDED,
            guidedWorkoutState = guidedState,
            actionErrorMessageRes = null
        )
    }

    fun saveSet(
        setSessionId: String,
        repsText: String,
        weightText: String,
        rirText: String
    ) {
        val currentState = currentSuccessOrNull() ?: return

        saveSetInternal(
            currentState = currentState,
            setSessionId = setSessionId,
            repsText = repsText,
            weightText = weightText,
            rirText = rirText,
            afterSuccess = { updatedSet ->
                replaceSetInCurrentSession(updatedSet)
            }
        )
    }

    fun saveCurrentGuidedSet(
        repsText: String,
        weightText: String,
        rirText: String
    ) {
        val currentState = currentSuccessOrNull() ?: return
        val guidedState = currentState.guidedWorkoutState

        if (guidedState.phase != GuidedWorkoutPhase.SET_INPUT) return

        val currentSet = currentState.session
            .getSetOrNull(
                exerciseIndex = guidedState.currentExerciseIndex,
                setIndex = guidedState.currentSetIndex
            ) ?: return

        saveSetInternal(
            currentState = currentState,
            setSessionId = currentSet.id,
            repsText = repsText,
            weightText = weightText,
            rirText = rirText,
            afterSuccess = { updatedSet ->
                replaceSetInCurrentSession(updatedSet)
                moveGuidedWorkoutAfterSetCompleted()
            }
        )
    }

    fun skipRest() {
        restTimerJob?.cancel()

        val currentState = currentSuccessOrNull() ?: return
        val guidedState = currentState.guidedWorkoutState

        if (
            guidedState.phase != GuidedWorkoutPhase.REST_BETWEEN_SETS &&
            guidedState.phase != GuidedWorkoutPhase.REST_BETWEEN_EXERCISES
        ) {
            return
        }

        _uiState.value = currentState.copy(
            guidedWorkoutState = guidedState.copy(
                phase = GuidedWorkoutPhase.SET_INPUT,
                remainingRestSeconds = 0,
                nextExerciseName = null
            )
        )
    }

    fun finishWorkoutSession(
        onFinished: () -> Unit
    ) {
        val currentState = currentSuccessOrNull() ?: return

        restTimerJob?.cancel()

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

    private fun saveSetInternal(
        currentState: WorkoutSessionUiState.Success,
        setSessionId: String,
        repsText: String,
        weightText: String,
        rirText: String,
        afterSuccess: (SetSession) -> Unit
    ) {
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
                    afterSuccess(updatedSet)
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

    private fun moveGuidedWorkoutAfterSetCompleted() {
        val currentState = currentSuccessOrNull() ?: return
        val guidedState = currentState.guidedWorkoutState
        val session = currentState.session

        val currentExercise = session.exercises.getOrNull(guidedState.currentExerciseIndex)
            ?: return

        val nextSetIndex = guidedState.currentSetIndex + 1

        if (nextSetIndex < currentExercise.sets.size) {
            val restSeconds = currentExercise.restBetweenSetsSeconds

            _uiState.value = currentState.copy(
                guidedWorkoutState = guidedState.copy(
                    currentSetIndex = nextSetIndex,
                    phase = GuidedWorkoutPhase.REST_BETWEEN_SETS,
                    remainingRestSeconds = restSeconds,
                    nextExerciseName = null
                )
            )

            startRestTimer()
            return
        }

        val nextExerciseIndex = guidedState.currentExerciseIndex + 1
        val nextExercise = session.exercises.getOrNull(nextExerciseIndex)

        if (nextExercise != null) {
            val restSeconds = currentExercise.restAfterExerciseSeconds

            _uiState.value = currentState.copy(
                guidedWorkoutState = guidedState.copy(
                    currentExerciseIndex = nextExerciseIndex,
                    currentSetIndex = 0,
                    phase = GuidedWorkoutPhase.REST_BETWEEN_EXERCISES,
                    remainingRestSeconds = restSeconds,
                    nextExerciseName = nextExercise.name
                )
            )

            startRestTimer()
            return
        }

        restTimerJob?.cancel()

        _uiState.value = currentState.copy(
            guidedWorkoutState = guidedState.copy(
                phase = GuidedWorkoutPhase.FINISHED,
                remainingRestSeconds = 0,
                nextExerciseName = null
            )
        )
    }

    private fun startRestTimer() {
        restTimerJob?.cancel()

        restTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)

                val currentState = currentSuccessOrNull() ?: return@launch
                val guidedState = currentState.guidedWorkoutState

                val isRestPhase =
                    guidedState.phase == GuidedWorkoutPhase.REST_BETWEEN_SETS ||
                            guidedState.phase == GuidedWorkoutPhase.REST_BETWEEN_EXERCISES

                if (!isRestPhase) return@launch

                val nextRemainingSeconds = guidedState.remainingRestSeconds - 1

                if (nextRemainingSeconds <= 0) {
                    _uiState.value = currentState.copy(
                        guidedWorkoutState = guidedState.copy(
                            phase = GuidedWorkoutPhase.SET_INPUT,
                            remainingRestSeconds = 0,
                            nextExerciseName = null
                        )
                    )
                    return@launch
                }

                _uiState.value = currentState.copy(
                    guidedWorkoutState = guidedState.copy(
                        remainingRestSeconds = nextRemainingSeconds
                    )
                )
            }
        }
    }

    private fun createInitialGuidedState(session: WorkoutSession): GuidedWorkoutState {
        val firstIncompletePosition = session.findFirstIncompleteSetPosition()

        return if (firstIncompletePosition == null) {
            GuidedWorkoutState(
                phase = GuidedWorkoutPhase.FINISHED
            )
        } else {
            GuidedWorkoutState(
                currentExerciseIndex = firstIncompletePosition.exerciseIndex,
                currentSetIndex = firstIncompletePosition.setIndex,
                phase = GuidedWorkoutPhase.SET_INPUT
            )
        }
    }

    private fun WorkoutSession.findFirstIncompleteSetPosition(): GuidedSetPosition? {
        exercises.forEachIndexed { exerciseIndex, exercise ->
            exercise.sets.forEachIndexed { setIndex, set ->
                if (!set.completed) {
                    return GuidedSetPosition(
                        exerciseIndex = exerciseIndex,
                        setIndex = setIndex
                    )
                }
            }
        }

        return null
    }

    private fun WorkoutSession.getSetOrNull(
        exerciseIndex: Int,
        setIndex: Int
    ): SetSession? {
        return exercises
            .getOrNull(exerciseIndex)
            ?.sets
            ?.getOrNull(setIndex)
    }

    private fun currentSuccessOrNull(): WorkoutSessionUiState.Success? {
        return _uiState.value as? WorkoutSessionUiState.Success
    }

    override fun onCleared() {
        restTimerJob?.cancel()
        super.onCleared()
    }

    private data class GuidedSetPosition(
        val exerciseIndex: Int,
        val setIndex: Int
    )
}
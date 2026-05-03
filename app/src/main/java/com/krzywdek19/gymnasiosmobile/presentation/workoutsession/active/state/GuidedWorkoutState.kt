package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.active.state

data class GuidedWorkoutState(
    val currentExerciseIndex: Int = 0,
    val currentSetIndex: Int = 0,
    val phase: GuidedWorkoutPhase = GuidedWorkoutPhase.SET_INPUT,
    val remainingRestSeconds: Int = 0,
    val nextExerciseName: String? = null
)
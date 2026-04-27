package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.active

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.core.ui.components.AccentBadge
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppGradientBackground
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTopBar
import com.krzywdek19.gymnasiosmobile.core.ui.components.EmptyStateCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.MetricTile
import com.krzywdek19.gymnasiosmobile.core.ui.components.PrimaryButton
import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseSession
import com.krzywdek19.gymnasiosmobile.domain.model.SetSession

@Composable
fun WorkoutSessionScreen(
    workoutSessionId: String,
    onBack: () -> Unit,
    viewModel: WorkoutSessionViewModel = viewModel(
        factory = WorkoutSessionViewModelFactory()
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(workoutSessionId) {
        viewModel.loadWorkoutSession(workoutSessionId)
    }

    AppGradientBackground {
        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.workout_session_title),
                    subtitle = stringResource(R.string.workout_session_subtitle),
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    navigationContentDescription = stringResource(R.string.back),
                    onNavigationClick = onBack
                )
            },
            bottomBar = {
                val state = uiState
                if (state is WorkoutSessionUiState.Success) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        PrimaryButton(
                            text = stringResource(R.string.finish_workout_session),
                            onClick = {
                                viewModel.finishWorkoutSession(
                                    onFinished = onBack
                                )
                            },
                            enabled = !state.isFinishingSession,
                            isLoading = state.isFinishingSession,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        ) { paddingValues ->
            when (val state = uiState) {
                WorkoutSessionUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is WorkoutSessionUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateCard(
                            title = stringResource(R.string.error_generic),
                            description = stringResource(state.messageRes)
                        )
                    }
                }

                is WorkoutSessionUiState.Success -> {
                    WorkoutSessionContent(
                        state = state,
                        paddingValues = paddingValues,
                        onSaveSet = viewModel::saveSet
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutSessionContent(
    state: WorkoutSessionUiState.Success,
    paddingValues: PaddingValues,
    onSaveSet: (
        setSessionId: String,
        repsText: String,
        weightText: String,
        rirText: String
    ) -> Unit
) {
    val session = state.session
    val exercises = session.exercises
    val totalSets = exercises.sumOf { it.sets.size }
    val completedSets = exercises.sumOf { exercise ->
        exercise.sets.count { it.completed }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 12.dp,
            bottom = 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                AccentBadge(
                    text = stringResource(R.string.workout_session_in_progress)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricTile(
                        label = stringResource(R.string.exercises_count_label),
                        value = exercises.size.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    MetricTile(
                        label = stringResource(R.string.completed_sets_label),
                        value = "$completedSets/$totalSets",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        state.actionErrorMessageRes?.let { errorMessageRes ->
            item {
                EmptyStateCard(
                    title = stringResource(R.string.error_generic),
                    description = stringResource(errorMessageRes)
                )
            }
        }

        if (exercises.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.no_exercises_in_session),
                    description = stringResource(R.string.no_exercises_in_session_description)
                )
            }
        } else {
            items(
                items = exercises,
                key = { it.id }
            ) { exercise ->
                ExerciseSessionCard(
                    exercise = exercise,
                    savingSetIds = state.savingSetIds,
                    onSaveSet = onSaveSet
                )
            }
        }
    }
}

@Composable
private fun ExerciseSessionCard(
    exercise: ExerciseSession,
    savingSetIds: Set<String>,
    onSaveSet: (
        setSessionId: String,
        repsText: String,
        weightText: String,
        rirText: String
    ) -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.sets_label) + ": ${exercise.setsCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        exercise.sets.forEach { set ->
            SetSessionRow(
                set = set,
                isSaving = savingSetIds.contains(set.id),
                onSaveSet = onSaveSet
            )
        }
    }
}

@Composable
private fun SetSessionRow(
    set: SetSession,
    isSaving: Boolean,
    onSaveSet: (
        setSessionId: String,
        repsText: String,
        weightText: String,
        rirText: String
    ) -> Unit
) {
    var repsText by remember(set.id, set.reps) {
        mutableStateOf(set.reps?.toString().orEmpty())
    }

    var weightText by remember(set.id, set.weight) {
        mutableStateOf(set.weight?.toString().orEmpty())
    }

    var rirText by remember(set.id, set.rir) {
        mutableStateOf(set.rir?.toString().orEmpty())
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.set_number, set.orderIndex),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = if (set.completed) {
                    stringResource(R.string.set_completed)
                } else {
                    stringResource(R.string.set_not_completed)
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (set.completed) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = repsText,
                onValueChange = { repsText = it },
                label = { Text(stringResource(R.string.reps_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text(stringResource(R.string.weight_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = rirText,
                onValueChange = { rirText = it },
                label = { Text(stringResource(R.string.rir_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        PrimaryButton(
            text = stringResource(R.string.save_set),
            onClick = {
                onSaveSet(
                    set.id,
                    repsText,
                    weightText,
                    rirText
                )
            },
            enabled = !isSaving,
            isLoading = isSaving,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
package com.krzywdek19.gymnasiosmobile.presentation.workouttemplate.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.di.AppContainer
import com.krzywdek19.gymnasiosmobile.domain.model.ExerciseTemplate
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

private data class PendingExerciseReorder(
    val exerciseId: String,
    val newOrder: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutTemplateDetailsScreen(
    workoutId: String,
    onBack: () -> Unit,
    onAddExerciseClick: (String) -> Unit,
    backStackEntry: NavBackStackEntry
) {
    val viewModel: WorkoutTemplateDetailsViewModel = viewModel(
        factory = WorkoutTemplateDetailsViewModelFactory(
            workoutTemplateRepository = AppContainer.workoutTemplateRepository,
            exerciseTemplateRepository = AppContainer.exerciseTemplateRepository,
            workoutId = workoutId
        )
    )

    val uiState by viewModel.uiState.collectAsState()

    val exerciseCreated by backStackEntry.savedStateHandle
        .getStateFlow("exercise_created", false)
        .collectAsState()

    LaunchedEffect(exerciseCreated) {
        if (exerciseCreated) {
            viewModel.loadData()
            backStackEntry.savedStateHandle["exercise_created"] = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.workout?.name
                            ?: stringResource(R.string.workout_details_title_fallback)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = uiState.errorMessage ?: stringResource(R.string.error_generic),
                        color = MaterialTheme.colorScheme.error
                    )

                    Button(onClick = { viewModel.loadData() }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }

            else -> {
                val initialExercises = remember(uiState.workout?.id, uiState.exercises) {
                    uiState.exercises.sortedBy { it.orderIndex }
                }

                var reorderedExercises by remember(uiState.workout?.id, uiState.exercises) {
                    mutableStateOf(initialExercises)
                }

                var pendingReorder by remember(uiState.workout?.id, uiState.exercises) {
                    mutableStateOf<PendingExerciseReorder?>(null)
                }

                val lazyListState = rememberLazyListState()
                val headerItemsCount = 2

                val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                    val fromExerciseIndex = from.index - headerItemsCount
                    val toExerciseIndex = to.index - headerItemsCount

                    if (
                        fromExerciseIndex !in reorderedExercises.indices ||
                        toExerciseIndex !in reorderedExercises.indices
                    ) {
                        return@rememberReorderableLazyListState
                    }

                    if (fromExerciseIndex == toExerciseIndex) {
                        return@rememberReorderableLazyListState
                    }

                    reorderedExercises = reorderedExercises.toMutableList().apply {
                        add(toExerciseIndex, removeAt(fromExerciseIndex))
                    }

                    val movedExercise = reorderedExercises[toExerciseIndex]
                    pendingReorder = PendingExerciseReorder(
                        exerciseId = movedExercise.id,
                        newOrder = toExerciseIndex + 1
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    state = lazyListState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = uiState.workout?.name.orEmpty(),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    item {
                        Button(
                            onClick = { onAddExerciseClick(workoutId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.add_exercise))
                        }
                    }

                    if (reorderedExercises.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.no_exercises_yet),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(
                            items = reorderedExercises,
                            key = { it.id }
                        ) { exercise ->
                            ReorderableItem(
                                state = reorderableLazyListState,
                                key = exercise.id
                            ) { isDragging ->
                                val currentIndex = reorderedExercises.indexOfFirst { it.id == exercise.id }

                                ExerciseRow(
                                    exercise = exercise,
                                    displayOrder = currentIndex + 1,
                                    isDragging = isDragging,
                                    modifier = with(this) {
                                        Modifier.longPressDraggableHandle(
                                            onDragStopped = {
                                                pendingReorder?.let { pending ->
                                                    viewModel.reorderExercise(
                                                        exerciseId = pending.exerciseId,
                                                        newOrder = pending.newOrder
                                                    )
                                                }
                                                pendingReorder = null
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseRow(
    exercise: ExerciseTemplate,
    displayOrder: Int,
    isDragging: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 10.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Kolejność: $displayOrder",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(
                    R.string.exercise_sets_reps_format,
                    exercise.setsCount,
                    exercise.reps
                ),
                style = MaterialTheme.typography.bodyMedium
            )

            if (!exercise.notes.isNullOrBlank()) {
                Text(
                    text = exercise.notes!!,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
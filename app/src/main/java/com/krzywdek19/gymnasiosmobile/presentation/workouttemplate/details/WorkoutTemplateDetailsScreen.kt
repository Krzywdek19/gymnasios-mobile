package com.krzywdek19.gymnasiosmobile.presentation.workouttemplate.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppGradientBackground
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppSectionHeader
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTopBar
import com.krzywdek19.gymnasiosmobile.core.ui.components.EmptyStateCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.MetricTile
import com.krzywdek19.gymnasiosmobile.core.ui.components.PrimaryButton
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
            AppTopBar(
                title = uiState.workout?.name ?: stringResource(R.string.workout_details_title_fallback),
                subtitle = stringResource(R.string.workout_details_subtitle),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                navigationContentDescription = stringResource(R.string.back),
                onNavigationClick = onBack
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { innerPadding ->
        AppGradientBackground {
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

                uiState.screenErrorMessageRes != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        EmptyStateCard(
                            title = stringResource(R.string.error_generic),
                            description = stringResource(uiState.screenErrorMessageRes!!)
                        )
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
                    val staticItemsBeforeExercises = 3 + if (uiState.actionErrorMessageRes != null) 1 else 0

                    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                        val fromExerciseIndex = from.index - staticItemsBeforeExercises
                        val toExerciseIndex = to.index - staticItemsBeforeExercises

                        if (
                            fromExerciseIndex !in reorderedExercises.indices ||
                            toExerciseIndex !in reorderedExercises.indices ||
                            fromExerciseIndex == toExerciseIndex
                        ) {
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
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 12.dp,
                            bottom = 32.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            AppCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = uiState.workout?.name.orEmpty(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    MetricTile(
                                        label = stringResource(R.string.exercise_count_label),
                                        value = reorderedExercises.size.toString(),
                                        modifier = Modifier.weight(1f)
                                    )
                                    MetricTile(
                                        label = stringResource(R.string.workout_name_metric),
                                        value = uiState.workout?.name.orEmpty(),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        item {
                            PrimaryButton(
                                text = stringResource(R.string.add_exercise),
                                onClick = { onAddExerciseClick(workoutId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item {
                            AppSectionHeader(title = stringResource(R.string.exercise_list_title))
                        }

                        uiState.actionErrorMessageRes?.let { errorRes ->
                            item {
                                EmptyStateCard(
                                    title = stringResource(R.string.error_generic),
                                    description = stringResource(errorRes)
                                )
                            }
                        }

                        if (reorderedExercises.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    title = stringResource(R.string.no_exercises_yet),
                                    description = stringResource(R.string.create_exercise_subtitle)
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
                                    if (currentIndex == -1) return@ReorderableItem

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
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDragging) 10.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricTile(
                    label = stringResource(R.string.exercise_position_label),
                    value = displayOrder.toString(),
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = stringResource(R.string.sets_count_label),
                    value = exercise.setsCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = stringResource(
                    R.string.exercise_sets_reps_format,
                    exercise.setsCount,
                    exercise.reps
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            exercise.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
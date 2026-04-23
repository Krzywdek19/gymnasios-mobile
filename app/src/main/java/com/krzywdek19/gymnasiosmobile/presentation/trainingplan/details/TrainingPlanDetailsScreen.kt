package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppGradientBackground
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppSectionHeader
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTopBar
import com.krzywdek19.gymnasiosmobile.core.ui.components.EmptyStateCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.MetricTile
import com.krzywdek19.gymnasiosmobile.core.ui.components.PrimaryButton
import com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details.components.WorkoutCard
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState

private data class PendingWorkoutReorder(
    val workoutId: String,
    val newOrder: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingPlanDetailsScreen(
    planId: String,
    onBack: () -> Unit,
    onWorkoutClick: (String) -> Unit,
    viewModel: TrainingPlanDetailsViewModel = viewModel(
        factory = TrainingPlanDetailsViewModelFactory()
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var workoutName by remember { mutableStateOf("") }
    var showRenameDialog by remember { mutableStateOf(false) }
    var editedWorkoutId by remember { mutableStateOf<String?>(null) }
    var editedWorkoutName by remember { mutableStateOf("") }

    LaunchedEffect(planId) {
        viewModel.loadPlan(planId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = when (val state = uiState) {
                    is TrainingPlanDetailsUiState.Success -> state.plan.name
                    else -> stringResource(R.string.training_plan_details_title)
                },
                subtitle = stringResource(R.string.training_plan_details_subtitle),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                navigationContentDescription = stringResource(R.string.back),
                onNavigationClick = onBack
            )
        },
        bottomBar = {
            if (uiState is TrainingPlanDetailsUiState.Success) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    PrimaryButton(
                        text = stringResource(R.string.add_workout),
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { innerPadding ->
        AppGradientBackground {
            when (val state = uiState) {
                is TrainingPlanDetailsUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is TrainingPlanDetailsUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateCard(
                            title = stringResource(R.string.error_generic),
                            description = stringResource(state.messageRes)
                        )
                    }
                }

                is TrainingPlanDetailsUiState.Success -> {
                    val plan = state.plan
                    val initialWorkouts = remember(plan.id, plan.workouts) {
                        plan.workouts.sortedBy { it.orderIndex }
                    }

                    var reorderedWorkouts by remember(plan.id, plan.workouts) {
                        mutableStateOf(initialWorkouts)
                    }

                    var pendingReorder by remember(plan.id, plan.workouts) {
                        mutableStateOf<PendingWorkoutReorder?>(null)
                    }

                    val lazyListState = rememberLazyListState()
                    val staticItemsBeforeWorkouts = 2 + if (state.actionErrorMessageRes != null) 1 else 0

                    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                        val fromIndex = from.index - staticItemsBeforeWorkouts
                        val toIndex = to.index - staticItemsBeforeWorkouts

                        if (fromIndex == toIndex) return@rememberReorderableLazyListState
                        if (fromIndex !in reorderedWorkouts.indices) return@rememberReorderableLazyListState
                        if (toIndex !in reorderedWorkouts.indices) return@rememberReorderableLazyListState

                        reorderedWorkouts = reorderedWorkouts.toMutableList().apply {
                            val movedItem = removeAt(fromIndex)
                            add(toIndex, movedItem)
                        }

                        val movedWorkout = reorderedWorkouts[toIndex]
                        pendingReorder = PendingWorkoutReorder(
                            workoutId = movedWorkout.id,
                            newOrder = toIndex + 1
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
                            bottom = 96.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            AppCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = plan.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    MetricTile(
                                        label = stringResource(R.string.workout_count_label),
                                        value = reorderedWorkouts.size.toString(),
                                        modifier = Modifier.weight(1f)
                                    )
                                    MetricTile(
                                        label = stringResource(R.string.next_workout_metric),
                                        value = reorderedWorkouts.firstOrNull()?.name?.takeIf { it.isNotBlank() }
                                            ?: stringResource(R.string.not_available),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        item {
                            AppSectionHeader(title = stringResource(R.string.other_workouts))
                        }

                        state.actionErrorMessageRes?.let { errorRes ->
                            item {
                                AppCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = stringResource(errorRes),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        if (reorderedWorkouts.isEmpty()) {
                            item {
                                EmptyWorkoutsCard()
                            }
                        } else {
                            items(
                                items = reorderedWorkouts,
                                key = { it.id }
                            ) { workout ->
                                ReorderableItem(
                                    state = reorderableLazyListState,
                                    key = workout.id
                                ) { isDragging ->
                                    val currentIndex = reorderedWorkouts.indexOfFirst { it.id == workout.id }
                                    if (currentIndex == -1) return@ReorderableItem

                                    val displayOrder = currentIndex + 1
                                    val isNextWorkout = currentIndex == 0

                                    WorkoutCard(
                                        workout = workout,
                                        displayOrder = displayOrder,
                                        isNextWorkout = isNextWorkout,
                                        isDragging = isDragging,
                                        modifier = with(this) {
                                            Modifier.longPressDraggableHandle(
                                                onDragStopped = {
                                                    pendingReorder?.let { pending ->
                                                        viewModel.reorderWorkout(
                                                            workoutId = pending.workoutId,
                                                            newOrder = pending.newOrder
                                                        )
                                                    }
                                                    pendingReorder = null
                                                }
                                            )
                                        },
                                        onClick = onWorkoutClick,
                                        onEdit = { selectedWorkout ->
                                            editedWorkoutId = selectedWorkout.id
                                            editedWorkoutName = selectedWorkout.name
                                            showRenameDialog = true
                                        },
                                        onDelete = { workoutId ->
                                            viewModel.deleteWorkout(workoutId)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (showAddDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showAddDialog = false
                                workoutName = ""
                            },
                            title = { Text(stringResource(R.string.new_workout)) },
                            text = {
                                OutlinedTextField(
                                    value = workoutName,
                                    onValueChange = { workoutName = it },
                                    label = { Text(stringResource(R.string.workout_name)) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        val trimmedName = workoutName.trim()
                                        if (trimmedName.isNotEmpty()) {
                                            viewModel.addWorkout(trimmedName)
                                            workoutName = ""
                                            showAddDialog = false
                                        }
                                    },
                                    enabled = workoutName.trim().isNotEmpty()
                                ) {
                                    Text(stringResource(R.string.add))
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showAddDialog = false
                                        workoutName = ""
                                    }
                                ) {
                                    Text(stringResource(R.string.cancel))
                                }
                            }
                        )
                    }

                    if (showRenameDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showRenameDialog = false
                                editedWorkoutId = null
                                editedWorkoutName = ""
                            },
                            title = { Text(stringResource(R.string.rename_workout_title)) },
                            text = {
                                OutlinedTextField(
                                    value = editedWorkoutName,
                                    onValueChange = { editedWorkoutName = it },
                                    label = { Text(stringResource(R.string.rename_workout_label)) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        val workoutId = editedWorkoutId
                                        val trimmedName = editedWorkoutName.trim()
                                        if (workoutId != null && trimmedName.isNotEmpty()) {
                                            viewModel.renameWorkout(
                                                workoutId = workoutId,
                                                newName = trimmedName
                                            )
                                            showRenameDialog = false
                                            editedWorkoutId = null
                                            editedWorkoutName = ""
                                        }
                                    },
                                    enabled = editedWorkoutName.trim().isNotEmpty()
                                ) {
                                    Text(stringResource(R.string.save))
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showRenameDialog = false
                                        editedWorkoutId = null
                                        editedWorkoutName = ""
                                    }
                                ) {
                                    Text(stringResource(R.string.cancel))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyWorkoutsCard() {
    EmptyStateCard(
        title = stringResource(R.string.no_workouts_in_plan),
        description = stringResource(R.string.add_first_workout_hint)
    )
}
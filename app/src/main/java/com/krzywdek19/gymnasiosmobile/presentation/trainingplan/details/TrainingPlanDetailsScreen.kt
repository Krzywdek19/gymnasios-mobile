package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
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
import com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details.components.WorkoutCard
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

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
            when (val state = uiState) {
                is TrainingPlanDetailsUiState.Success -> {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = state.plan.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                StatusChip(planStatus = state.plan.status)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back)
                                )
                            }
                        }
                    )
                }

                else -> {
                    TopAppBar(
                        title = {
                            Text(stringResource(R.string.training_plan_details_title))
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back)
                                )
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (uiState is TrainingPlanDetailsUiState.Success) {
                Surface {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = { showAddDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.add_workout))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
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
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(state.messageRes))
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
                val listStartOffset = 1

                val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
                    val fromIndex = from.index - listStartOffset
                    val toIndex = to.index - listStartOffset

                    if (fromIndex == toIndex) return@rememberReorderableLazyListState

                    if (fromIndex !in reorderedWorkouts.indices) return@rememberReorderableLazyListState
                    if (toIndex !in 0..reorderedWorkouts.size) return@rememberReorderableLazyListState

                    reorderedWorkouts = reorderedWorkouts.toMutableList().apply {
                        val movedItem = removeAt(fromIndex)
                        add(toIndex.coerceAtMost(size), movedItem)
                    }

                    val finalIndex = toIndex.coerceIn(0, reorderedWorkouts.lastIndex)
                    val movedWorkout = reorderedWorkouts[finalIndex]

                    pendingReorder = PendingWorkoutReorder(
                        workoutId = movedWorkout.id,
                        newOrder = finalIndex + 1
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    state = lazyListState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (reorderedWorkouts.isEmpty()) {
                        item {
                            EmptyWorkoutsCard()
                        }
                    } else {
                        item {
                            Text(
                                text = stringResource(R.string.other_workouts),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                        }

                        items(
                            items = reorderedWorkouts,
                            key = { it.id }
                        ) { workout ->
                            ReorderableItem(
                                state = reorderableLazyListState,
                                key = workout.id
                            ) { isDragging ->

                                val currentIndex = reorderedWorkouts.indexOfFirst { it.id == workout.id }
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
                        title = {
                            Text(stringResource(R.string.new_workout))
                        },
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
                        title = {
                            Text(stringResource(R.string.rename_workout_title))
                        },
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

@Composable
private fun StatusChip(planStatus: String) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = planStatus,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun EmptyWorkoutsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.no_workouts_in_plan),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.add_first_workout_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
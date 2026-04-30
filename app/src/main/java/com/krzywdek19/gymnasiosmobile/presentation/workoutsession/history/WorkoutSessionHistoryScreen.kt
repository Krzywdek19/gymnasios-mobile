package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.core.ui.components.AccentBadge
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppGradientBackground
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTopBar
import com.krzywdek19.gymnasiosmobile.core.ui.components.EmptyStateCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.LogoutAction
import com.krzywdek19.gymnasiosmobile.core.ui.components.MetricTile
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSession
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSessionStatus

@Composable
fun WorkoutSessionHistoryScreen(
    onBack: () -> Unit,
    onSessionClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: WorkoutSessionHistoryViewModel = viewModel(
        factory = WorkoutSessionHistoryViewModelFactory()
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    var sessionToDelete by remember { mutableStateOf<WorkoutSession?>(null) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadWorkoutSessions()
    }

    AppGradientBackground {
        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.workout_session_history_title),
                    subtitle = stringResource(R.string.workout_session_history_subtitle),
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    navigationContentDescription = stringResource(R.string.back),
                    onNavigationClick = onBack,
                    actions = {
                        LogoutAction(onLogoutClick = onLogoutClick)
                    }
                )
            }
        ) { paddingValues ->
            when (val state = uiState) {
                WorkoutSessionHistoryUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is WorkoutSessionHistoryUiState.Error -> {
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

                is WorkoutSessionHistoryUiState.Success -> {
                    WorkoutSessionHistoryContent(
                        state = state,
                        paddingValues = paddingValues,
                        onSessionClick = onSessionClick,
                        onDeleteSessionClick = { selectedSession ->
                            sessionToDelete = selectedSession
                        },
                        onClearHistoryClick = {
                            showClearHistoryDialog = true
                        }
                    )
                }
            }
        }
    }

    sessionToDelete?.let { session ->
        AlertDialog(
            onDismissRequest = {
                sessionToDelete = null
            },
            title = {
                Text(text = stringResource(R.string.delete_workout_session_title))
            },
            text = {
                Text(text = stringResource(R.string.delete_workout_session_confirmation))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWorkoutSession(session.id)
                        sessionToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        sessionToDelete = null
                    }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = {
                showClearHistoryDialog = false
            },
            title = {
                Text(text = stringResource(R.string.clear_workout_history_title))
            },
            text = {
                Text(text = stringResource(R.string.clear_workout_history_confirmation))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearFinishedWorkoutHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearHistoryDialog = false
                    }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun WorkoutSessionHistoryContent(
    state: WorkoutSessionHistoryUiState.Success,
    paddingValues: PaddingValues,
    onSessionClick: (String) -> Unit,
    onDeleteSessionClick: (WorkoutSession) -> Unit,
    onClearHistoryClick: () -> Unit
) {
    val sessions = state.sessions
    val hasFinishedSessions = sessions.any { it.status == WorkoutSessionStatus.FINISHED }

    if (sessions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateCard(
                title = stringResource(R.string.workout_session_history_empty_title),
                description = stringResource(R.string.workout_session_history_empty_description)
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 12.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.workout_session_history_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = stringResource(R.string.workout_session_history_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    DangerOutlinedButton(
                        text = stringResource(R.string.clear_workout_history),
                        onClick = onClearHistoryClick,
                        enabled = hasFinishedSessions && !state.isClearingHistory,
                        modifier = Modifier.fillMaxWidth()
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

        items(
            items = sessions,
            key = { it.id }
        ) { session ->
            WorkoutSessionHistoryCard(
                session = session,
                isDeleting = state.deletingSessionIds.contains(session.id),
                onClick = {
                    onSessionClick(session.id)
                },
                onDeleteClick = {
                    onDeleteSessionClick(session)
                }
            )
        }
    }
}

@Composable
private fun WorkoutSessionHistoryCard(
    session: WorkoutSession,
    isDeleting: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val completedSets = session.exercises.sumOf { exercise ->
        exercise.sets.count { it.completed }
    }

    val totalSets = session.exercises.sumOf { exercise ->
        exercise.sets.size
    }

    val statusLabel = when (session.status) {
        WorkoutSessionStatus.IN_PROGRESS -> stringResource(R.string.active_session_label)
        WorkoutSessionStatus.FINISHED -> stringResource(R.string.finished_session_label)
        WorkoutSessionStatus.UNKNOWN -> stringResource(R.string.status)
    }

    val workoutName = session.workoutTemplateName.ifBlank {
        stringResource(R.string.unknown_workout_name)
    }

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                AccentBadge(text = statusLabel)

                IconButton(
                    onClick = onDeleteClick,
                    enabled = !isDeleting
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_workout_session),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Text(
                text = workoutName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = buildSessionDateText(session),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricTile(
                    label = stringResource(R.string.exercises_count_label),
                    value = session.exercises.size.toString(),
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
}

@Composable
private fun DangerOutlinedButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.65f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.24f)
            }
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error,
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
        ),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        )
    ) {
        Text(text = text)
    }
}

private fun buildSessionDateText(session: WorkoutSession): String {
    val startedAt = formatSessionDate(session.startedAt)
    val finishedAt = formatSessionDate(session.finishedAt)

    return if (finishedAt.isBlank()) {
        startedAt
    } else {
        "$startedAt - $finishedAt"
    }
}

private fun formatSessionDate(value: String?): String {
    if (value.isNullOrBlank()) return ""

    return value
        .replace("T", " ")
        .replace("Z", "")
        .take(16)
}
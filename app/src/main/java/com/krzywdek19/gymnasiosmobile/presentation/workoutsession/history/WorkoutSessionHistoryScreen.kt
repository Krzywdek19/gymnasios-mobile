package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.history

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.krzywdek19.gymnasiosmobile.core.ui.components.MetricTile
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSession
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutSessionStatus

@Composable
fun WorkoutSessionHistoryScreen(
    onBack: () -> Unit,
    onSessionClick: (String) -> Unit,
    viewModel: WorkoutSessionHistoryViewModel = viewModel(
        factory = WorkoutSessionHistoryViewModelFactory()
    )
) {
    val uiState by viewModel.uiState.collectAsState()

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
                    onNavigationClick = onBack
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
                        sessions = state.sessions,
                        paddingValues = paddingValues,
                        onSessionClick = onSessionClick
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutSessionHistoryContent(
    sessions: List<WorkoutSession>,
    paddingValues: PaddingValues,
    onSessionClick: (String) -> Unit
) {
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
        items(
            items = sessions,
            key = { it.id }
        ) { session ->
            WorkoutSessionHistoryCard(
                session = session,
                onClick = {
                    onSessionClick(session.id)
                }
            )
        }
    }
}

@Composable
private fun WorkoutSessionHistoryCard(
    session: WorkoutSession,
    onClick: () -> Unit
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
        stringResource(R.string.workout_details_title_fallback)
    }

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AccentBadge(text = statusLabel)

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
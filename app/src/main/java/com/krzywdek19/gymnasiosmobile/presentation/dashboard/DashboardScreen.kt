package com.krzywdek19.gymnasiosmobile.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppGradientBackground
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTopBar
import com.krzywdek19.gymnasiosmobile.core.ui.components.EmptyStateCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.LogoutAction
import com.krzywdek19.gymnasiosmobile.core.ui.components.MetricTile
import com.krzywdek19.gymnasiosmobile.core.ui.components.PrimaryButton

@Composable
fun DashboardScreen(
    onTrainingPlansClick: () -> Unit,
    onWorkoutHistoryClick: () -> Unit,
    onWorkoutSessionReady: (String) -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory()
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    AppGradientBackground {
        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.dashboard_title),
                    subtitle = stringResource(R.string.dashboard_subtitle),
                    actions = {
                        LogoutAction(onLogoutClick = onLogoutClick)
                    }
                )
            }
        ) { paddingValues ->
            when (val state = uiState) {
                DashboardUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is DashboardUiState.Error -> {
                    DashboardErrorContent(
                        paddingValues = paddingValues,
                        messageRes = state.messageRes,
                        onTrainingPlansClick = onTrainingPlansClick
                    )
                }

                is DashboardUiState.Success -> {
                    DashboardContent(
                        state = state,
                        paddingValues = paddingValues,
                        onStartOrContinueWorkout = {
                            viewModel.startOrContinueWorkout(
                                onWorkoutSessionReady = onWorkoutSessionReady
                            )
                        },
                        onTrainingPlansClick = onTrainingPlansClick,
                        onWorkoutHistoryClick = onWorkoutHistoryClick
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardUiState.Success,
    paddingValues: PaddingValues,
    onStartOrContinueWorkout: () -> Unit,
    onTrainingPlansClick: () -> Unit,
    onWorkoutHistoryClick: () -> Unit
) {
    val hasActiveSession = state.activeWorkoutSessionId != null
    val hasActivePlan = state.activePlanId != null

    val workoutLabel = if (hasActiveSession) {
        stringResource(R.string.current_workout_label)
    } else {
        stringResource(R.string.next_workout_label)
    }

    val workoutName = state.workoutName.ifBlank {
        stringResource(R.string.not_available)
    }

    val mainButtonText = if (hasActiveSession) {
        stringResource(R.string.continue_workout_session)
    } else {
        stringResource(R.string.start_workout_session)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!hasActivePlan) {
            EmptyStateCard(
                title = stringResource(R.string.no_active_plan_title),
                description = stringResource(R.string.no_active_plan_description)
            )
        } else {
            AppCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricTile(
                        label = stringResource(R.string.active_plan_label),
                        value = state.activePlanName.ifBlank {
                            stringResource(R.string.not_available)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    MetricTile(
                        label = workoutLabel,
                        value = workoutName,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        state.actionErrorMessageRes?.let { errorMessageRes ->
            EmptyStateCard(
                title = stringResource(R.string.error_generic),
                description = stringResource(errorMessageRes)
            )
        }

        AppCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (hasActivePlan) {
                    PrimaryButton(
                        text = mainButtonText,
                        onClick = onStartOrContinueWorkout,
                        enabled = state.canStartWorkout && !state.isStartingWorkout,
                        isLoading = state.isStartingWorkout,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                PrimaryButton(
                    text = stringResource(R.string.training_plans_title),
                    onClick = onTrainingPlansClick,
                    modifier = Modifier.fillMaxWidth()
                )

                SecondaryDashboardButton(
                    text = stringResource(R.string.workout_session_history_title),
                    onClick = onWorkoutHistoryClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DashboardErrorContent(
    paddingValues: PaddingValues,
    messageRes: Int,
    onTrainingPlansClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyStateCard(
            title = stringResource(R.string.error_generic),
            description = stringResource(messageRes)
        )

        PrimaryButton(
            text = stringResource(R.string.training_plans_title),
            onClick = onTrainingPlansClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}

@Composable
private fun SecondaryDashboardButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        )
    ) {
        Text(text = text)
    }
}
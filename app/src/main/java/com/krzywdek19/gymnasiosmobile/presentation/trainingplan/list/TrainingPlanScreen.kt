package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NavBackStackEntry
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppGradientBackground
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTopBar
import com.krzywdek19.gymnasiosmobile.core.ui.components.EmptyStateCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.LogoutAction
import com.krzywdek19.gymnasiosmobile.domain.model.TrainingPlan

@Composable
fun TrainingPlanScreen(
    backStackEntry: NavBackStackEntry,
    onPlanClick: (String) -> Unit,
    onAddClick: () -> Unit,
    onBackToDashboard: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: TrainingPlanViewModel = viewModel(
        factory = TrainingPlanViewModelFactory()
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val planCreated by backStackEntry.savedStateHandle
        .getStateFlow("plan_created", false)
        .collectAsState()
    var planToDelete by remember { mutableStateOf<TrainingPlan?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadPlans()
    }

    LaunchedEffect(planCreated) {
        if (planCreated) {
            viewModel.loadPlans()
            backStackEntry.savedStateHandle["plan_created"] = false
        }
    }

    AppGradientBackground {
        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.training_plans_header),
                    subtitle = stringResource(R.string.training_plans_subtitle),
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    navigationContentDescription = stringResource(R.string.back),
                    onNavigationClick = onBackToDashboard,
                    actions = {
                        LogoutAction(onLogoutClick = onLogoutClick)
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
                    modifier = Modifier.navigationBarsPadding(),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.fab_add_plan_description)
                    )
                }
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { innerPadding ->
            when (val state = uiState) {
                is TrainingPlanUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is TrainingPlanUiState.Error -> {
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

                is TrainingPlanUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 16.dp,
                            bottom = 96.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (state.plans.isEmpty()) {
                            item {
                                EmptyStateCard(
                                    title = stringResource(R.string.training_plan_empty_title),
                                    description = stringResource(R.string.training_plan_empty_description)
                                )
                            }
                        } else {
                            items(
                                items = state.plans,
                                key = { it.id }
                            ) { plan ->
                                TrainingPlanItem(
                                    plan = plan,
                                    onClick = { selectedPlan ->
                                        onPlanClick(selectedPlan.id)
                                    },
                                    onDeleteClick = { selectedPlan ->
                                        planToDelete = selectedPlan
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        planToDelete?.let { plan ->
            AlertDialog(
                onDismissRequest = {
                    planToDelete = null
                },
                title = {
                    Text(text = stringResource(R.string.delete_plan_title))
                },
                text = {
                    Text(text = stringResource(R.string.delete_plan_confirmation))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deletePlan(plan.id)
                            planToDelete = null
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
                            planToDelete = null
                        }
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}
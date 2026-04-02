package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import com.krzywdek19.gymnasiosmobile.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TrainingPlanScreen(onPlanClick: (String) -> Unit, onAddClick: () -> Unit, viewModel: TrainingPlanViewModel = viewModel(
    factory = TrainingPlanViewModelFactory()
)) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPlans()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.training_plans_title)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->

        when (uiState) {

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

            is TrainingPlanUiState.Success -> {
                val plans = (uiState as TrainingPlanUiState.Success).plans

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(plans) { plan ->
                        TrainingPlanItem(
                            plan = plan,
                            onClick = {
                                onPlanClick(it.id)
                            }
                        )
                    }
                }
            }

            is TrainingPlanUiState.Error -> {
                val errorRes = (uiState as TrainingPlanUiState.Error).messageRes

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(errorRes))
                }
            }
        }
    }
}
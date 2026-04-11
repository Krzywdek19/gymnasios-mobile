package com.krzywdek19.gymnasiosmobile.presentation.exercisetemplate.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExerciseTemplateScreen(
    workoutTemplateId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: CreateExerciseTemplateViewModel = viewModel(
        factory = CreateExerciseTemplateViewModelFactory(
            exerciseTemplateRepository = AppContainer.exerciseTemplateRepository,
            workoutTemplateId = workoutTemplateId
        )
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) {
            viewModel.clearSuccessFlag()
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_exercise_title)) },
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.exercise_name_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.setsCount,
                onValueChange = viewModel::onSetsCountChange,
                label = { Text(stringResource(R.string.sets_count_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.reps,
                onValueChange = viewModel::onRepsChange,
                label = { Text(stringResource(R.string.reps_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text(stringResource(R.string.notes_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            uiState.errorMessageRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = { viewModel.saveExercise(orderIndex = 1) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}
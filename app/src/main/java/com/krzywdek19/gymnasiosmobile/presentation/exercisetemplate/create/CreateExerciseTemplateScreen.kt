package com.krzywdek19.gymnasiosmobile.presentation.exercisetemplate.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppGradientBackground
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTextField
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTopBar
import com.krzywdek19.gymnasiosmobile.core.ui.components.PrimaryButton
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
            AppTopBar(
                title = stringResource(R.string.add_exercise_title),
                subtitle = stringResource(R.string.create_exercise_subtitle),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                navigationContentDescription = stringResource(R.string.back),
                onNavigationClick = onBack
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { innerPadding ->
        AppGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    AppTextField(
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                        label = stringResource(R.string.exercise_name_label)
                    )

                    AppTextField(
                        value = uiState.setsCount,
                        onValueChange = viewModel::onSetsCountChange,
                        label = stringResource(R.string.sets_count_label)
                    )

                    AppTextField(
                        value = uiState.reps,
                        onValueChange = viewModel::onRepsChange,
                        label = stringResource(R.string.reps_label)
                    )

                    AppTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::onNotesChange,
                        label = stringResource(R.string.notes_label),
                        singleLine = false
                    )

                    uiState.errorMessageRes?.let { errorRes ->
                        Text(
                            text = stringResource(errorRes),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    PrimaryButton(
                        text = stringResource(R.string.create_exercise_button),
                        onClick = { viewModel.saveExercise(orderIndex = 1) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving,
                        isLoading = uiState.isSaving
                    )
                }
            }
        }
    }
}
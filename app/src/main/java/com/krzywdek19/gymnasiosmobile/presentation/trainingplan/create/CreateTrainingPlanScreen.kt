package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppGradientBackground
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTextField
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTopBar
import com.krzywdek19.gymnasiosmobile.core.ui.components.LogoutAction
import com.krzywdek19.gymnasiosmobile.core.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTrainingPlanScreen(
    onSave: () -> Unit,
    onBack: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: CreateTrainingPlanViewModel = viewModel(
        factory = CreateTrainingPlanViewModelFactory()
    )
) {
    LaunchedEffect(viewModel.savedSuccessfully) {
        if (viewModel.savedSuccessfully) {
            viewModel.clearSuccessFlag()
            onSave()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.create_training_plan_title),
                subtitle = stringResource(R.string.create_training_plan_subtitle),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                navigationContentDescription = stringResource(R.string.back),
                onNavigationClick = onBack,
                actions = {
                    LogoutAction(onLogoutClick = onLogoutClick)
                }
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
            ) {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    AppTextField(
                        value = viewModel.name,
                        onValueChange = viewModel::onNameChange,
                        label = stringResource(R.string.training_plan_name_label)
                    )

                    viewModel.errorMessageRes?.let { errorRes ->
                        Text(
                            text = stringResource(errorRes),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    PrimaryButton(
                        text = stringResource(R.string.create_training_plan_button),
                        onClick = viewModel::createPlan,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.isSaving,
                        isLoading = viewModel.isSaving
                    )
                }
            }
        }
    }
}
package com.krzywdek19.gymnasiosmobile.presentation.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.core.ui.components.AccentBadge
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppCard
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppGradientBackground
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTextField
import com.krzywdek19.gymnasiosmobile.core.ui.components.PrimaryButton
import com.krzywdek19.gymnasiosmobile.core.ui.components.SecondaryActionText
import com.krzywdek19.gymnasiosmobile.core.ui.theme.AppTextPrimary

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }

    AppGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 56.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            AccentBadge(text = stringResource(R.string.register_badge))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineLarge,
                color = AppTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.register_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            AppCard(modifier = Modifier.fillMaxWidth()) {
                AppTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = stringResource(R.string.email_label)
                )

                AppTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = stringResource(R.string.password_label),
                    visualTransformation = PasswordVisualTransformation()
                )

                Text(
                    text = stringResource(R.string.password_requirements_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AppTextField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = stringResource(R.string.confirm_password_label),
                    visualTransformation = PasswordVisualTransformation()
                )

                uiState.errorMessageRes?.let { errorRes ->
                    Text(
                        text = stringResource(errorRes),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                PrimaryButton(
                    text = stringResource(R.string.register_action),
                    onClick = viewModel::register,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading
                )

                SecondaryActionText(
                    text = stringResource(R.string.register_go_to_login),
                    onClick = onGoToLogin,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
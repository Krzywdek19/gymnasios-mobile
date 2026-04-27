package com.krzywdek19.gymnasiosmobile.presentation.workoutsession.active

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppGradientBackground
import com.krzywdek19.gymnasiosmobile.core.ui.components.AppTopBar

@Composable
fun WorkoutSessionScreen(
    workoutSessionId: String,
    onBack: () -> Unit
) {
    AppGradientBackground {
        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                AppTopBar(
                    title = stringResource(R.string.workout_session_title),
                    subtitle = stringResource(R.string.workout_session_subtitle),
                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                    navigationContentDescription = stringResource(R.string.back),
                    onNavigationClick = onBack
                )
            }
        ) { paddingValues: PaddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Session ID: $workoutSessionId",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
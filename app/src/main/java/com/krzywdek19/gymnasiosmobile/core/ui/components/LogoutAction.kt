package com.krzywdek19.gymnasiosmobile.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.krzywdek19.gymnasiosmobile.R

@Composable
fun LogoutAction(
    onLogoutClick: () -> Unit
) {
    TextButton(
        onClick = onLogoutClick
    ) {
        Text(
            text = stringResource(R.string.logout)
        )
    }
}
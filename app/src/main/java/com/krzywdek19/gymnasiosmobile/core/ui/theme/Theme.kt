package com.krzywdek19.gymnasiosmobile.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GymnasiosDarkColorScheme = darkColorScheme(
    primary = AppPrimary,
    onPrimary = AppOnPrimary,
    primaryContainer = AppPrimaryContainer,
    onPrimaryContainer = AppPrimary,
    secondary = AppTextSecondary,
    onSecondary = AppBackground,
    secondaryContainer = AppSurfaceElevated,
    onSecondaryContainer = AppTextPrimary,
    tertiary = AppSuccess,
    onTertiary = AppBackground,
    background = AppBackground,
    onBackground = AppTextPrimary,
    surface = AppSurface,
    onSurface = AppTextPrimary,
    surfaceVariant = AppSurfaceMuted,
    onSurfaceVariant = AppTextSecondary,
    outline = AppOutline,
    error = AppError,
    onError = AppBackground
)

@Composable
fun GymnasiosMobileTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GymnasiosDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}

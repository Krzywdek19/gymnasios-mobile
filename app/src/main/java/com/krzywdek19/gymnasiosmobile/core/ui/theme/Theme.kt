package com.krzywdek19.gymnasiosmobile.core.ui.theme

import androidx.compose.material3.Typography
import com.krzywdek19.gymnasiosmobile.ui.theme.GoldSoft
import com.krzywdek19.gymnasiosmobile.ui.theme.Ink
import com.krzywdek19.gymnasiosmobile.ui.theme.Paper
import com.krzywdek19.gymnasiosmobile.ui.theme.Plum700
import com.krzywdek19.gymnasiosmobile.ui.theme.Plum800
import com.krzywdek19.gymnasiosmobile.ui.theme.SakuraPink100
import com.krzywdek19.gymnasiosmobile.ui.theme.SakuraPink200
import com.krzywdek19.gymnasiosmobile.ui.theme.SakuraPink300
import com.krzywdek19.gymnasiosmobile.ui.theme.SakuraPink400
import com.krzywdek19.gymnasiosmobile.ui.theme.SakuraPink500
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
val Typography = Typography()

private val SakuraLightColorScheme = lightColorScheme(
    primary = SakuraPink500,
    onPrimary = Paper,
    primaryContainer = SakuraPink200,
    onPrimaryContainer = Plum800,

    secondary = Plum700,
    onSecondary = Paper,
    secondaryContainer = SakuraPink100,
    onSecondaryContainer = Ink,

    tertiary = GoldSoft,
    onTertiary = Ink,

    background = Paper,
    onBackground = Ink,

    surface = Paper,
    onSurface = Ink,

    surfaceVariant = SakuraPink100,
    onSurfaceVariant = Plum700,

    outline = SakuraPink300,
    error = Color(0xFFB3261E)
)

private val SakuraDarkColorScheme = darkColorScheme(
    primary = SakuraPink300,
    onPrimary = Ink,
    primaryContainer = Plum700,
    onPrimaryContainer = SakuraPink100,

    secondary = SakuraPink200,
    onSecondary = Ink,
    secondaryContainer = Plum800,
    onSecondaryContainer = SakuraPink100,

    tertiary = GoldSoft,
    onTertiary = Ink,

    background = Color(0xFF1D171B),
    onBackground = SakuraPink100,

    surface = Color(0xFF1D171B),
    onSurface = SakuraPink100,

    surfaceVariant = Color(0xFF3B2C34),
    onSurfaceVariant = SakuraPink200,

    outline = SakuraPink400,
    error = Color(0xFFF2B8B5)
)

@Composable
fun GymnasiosMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SakuraDarkColorScheme else SakuraLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
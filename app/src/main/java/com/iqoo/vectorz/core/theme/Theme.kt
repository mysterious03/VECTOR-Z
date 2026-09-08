package com.iqoo.vectorz.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val VectorZBlack = Color(0xFF06070A)
val VectorZSurface = Color(0xFF0F111A)
val VectorZSurfaceElevated = Color(0xFF161A29)
val VectorZCard = Color(0xFF12141F)
val VectorZCyan = Color(0xFF00E5FF)
val VectorZBlue = Color(0xFF3D5AFE)
val VectorZSafetyGreen = Color(0xFF00E676)
val VectorZWarningAmber = Color(0xFFFFD600)
val VectorZRiskRed = Color(0xFFFF1744)
val VectorZTextPrimary = Color(0xFFF5F5F7)
val VectorZTextSecondary = Color(0xFF9E9EA7)
val VectorZBorder = Color(0xFF24293D)

private val DarkColorScheme = darkColorScheme(
    primary = VectorZCyan,
    onPrimary = VectorZBlack,
    primaryContainer = VectorZSurfaceElevated,
    onPrimaryContainer = VectorZCyan,
    secondary = VectorZBlue,
    onSecondary = Color.White,
    background = VectorZBlack,
    onBackground = VectorZTextPrimary,
    surface = VectorZSurface,
    onSurface = VectorZTextPrimary,
    surfaceVariant = VectorZCard,
    onSurfaceVariant = VectorZTextSecondary,
    error = VectorZRiskRed,
    onError = Color.White
)

@Composable
fun VectorZTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

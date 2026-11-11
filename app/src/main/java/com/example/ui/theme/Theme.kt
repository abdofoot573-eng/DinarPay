package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PayDzLightColorScheme = lightColorScheme(
    primary = PayDzBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEAFE),
    onPrimaryContainer = PayDzNavyText,
    secondary = PayDzGold,
    onSecondary = PayDzNavyText,
    secondaryContainer = PayDzGoldSoft,
    onSecondaryContainer = PayDzNavyText,
    tertiary = PayDzGreen,
    onTertiary = Color.White,
    background = PayDzBgSoft,
    onBackground = PayDzNavyText,
    surface = PayDzCardWhite,
    onSurface = PayDzNavyText,
    surfaceVariant = PayDzInputBg,
    onSurfaceVariant = PayDzSlateText,
    error = PayDzRed,
    onError = Color.White
)

@Composable
fun ApplicationTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always use the bright, high-contrast PayDz light theme to match the exact uploaded UI design
    MaterialTheme(
        colorScheme = PayDzLightColorScheme,
        typography = Typography,
        content = content
    )
}

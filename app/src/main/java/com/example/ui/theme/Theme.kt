package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BiaarDarkColorScheme = darkColorScheme(
    primary = BiaarEmerald,
    onPrimary = Color.Black,
    primaryContainer = BiaarEmeraldDark,
    onPrimaryContainer = TextPrimary,
    secondary = BiaarGold,
    onSecondary = Color.Black,
    secondaryContainer = BiaarGoldDark,
    onSecondaryContainer = TextPrimary,
    tertiary = BiaarCyan,
    onTertiary = Color.Black,
    background = BiaarDarkBg,
    onBackground = TextPrimary,
    surface = BiaarDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = BiaarDarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = BiaarRuby,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BiaarDarkColorScheme,
        typography = Typography,
        content = content
    )
}

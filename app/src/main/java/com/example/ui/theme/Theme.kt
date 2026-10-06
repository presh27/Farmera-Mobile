package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FarmeraLightColorScheme = lightColorScheme(
    primary = FarmeraGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = FarmeraMintSurface,
    onPrimaryContainer = FarmeraGreenDark,
    secondary = FarmeraGreenDark,
    onSecondary = Color.White,
    secondaryContainer = FarmeraMintCard,
    onSecondaryContainer = FarmeraGreenDark,
    background = FarmeraWhite,
    onBackground = FarmeraTextPrimary,
    surface = FarmeraWhite,
    onSurface = FarmeraTextPrimary,
    surfaceVariant = FarmeraSurfaceNeutral,
    onSurfaceVariant = FarmeraTextSecondary,
    outline = FarmeraBorder,
    outlineVariant = FarmeraBorderLight
)

private val FarmeraDarkColorScheme = darkColorScheme(
    primary = FarmeraGreenLight,
    onPrimary = Color.Black,
    primaryContainer = FarmeraGreenDark,
    onPrimaryContainer = Color.White,
    background = Color(0xFF121417),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF181B1F),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF22262B),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF374151),
    outlineVariant = Color(0xFF1F2937)
)

@Composable
fun FarmeraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Farmera strictly uses its signature clean white and fresh green palette
    val colorScheme = if (darkTheme) FarmeraDarkColorScheme else FarmeraLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

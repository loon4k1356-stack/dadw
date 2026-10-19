package com.example.stealthapphider.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StealthColors = darkColorScheme(
    primary = Color(0xFFB7A1FF),
    onPrimary = Color(0xFF25105E),
    primaryContainer = Color(0xFF3A2675),
    onPrimaryContainer = Color(0xFFE9DDFF),
    secondary = Color(0xFF9ED9D3),
    onSecondary = Color(0xFF003735),
    secondaryContainer = Color(0xFF1D4E4B),
    onSecondaryContainer = Color(0xFFBAF2EC),
    background = Color(0xFF101118),
    onBackground = Color(0xFFE5E1EC),
    surface = Color(0xFF171821),
    onSurface = Color(0xFFE5E1EC),
    surfaceVariant = Color(0xFF292833),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
)

@Composable
fun StealthAppHiderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StealthColors,
        content = content,
    )
}

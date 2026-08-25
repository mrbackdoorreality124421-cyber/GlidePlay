package com.smoothplay.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF10B981),
    secondary = Color(0xFF047857),
    background = Color(0xFF09090B),
    surface = Color(0xFF18181B),
    onPrimary = Color(0xFF052E16),
    onBackground = Color(0xFFF4F4F5),
    onSurface = Color(0xFFF4F4F5)
)

@Composable
fun SmoothPlayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

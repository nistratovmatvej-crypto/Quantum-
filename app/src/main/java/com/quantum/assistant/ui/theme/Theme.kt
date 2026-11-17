package com.quantum.assistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object QuantumColors {
    val Navy = Color(0xFF070B1A)
    val NavySurface = Color(0xFF0E1630)
    val NavyOutline = Color(0xFF22305A)
    val Cyan = Color(0xFF4FD8F0)
    val StarWhite = Color(0xFFFFFFFF)
    val TextMuted = Color(0xFF9AA7C7)
}

private val Scheme = darkColorScheme(
    primary = QuantumColors.Cyan,
    onPrimary = QuantumColors.Navy,
    background = QuantumColors.Navy,
    onBackground = QuantumColors.StarWhite,
    surface = QuantumColors.NavySurface,
    onSurface = QuantumColors.StarWhite,
    surfaceVariant = QuantumColors.NavySurface,
    onSurfaceVariant = QuantumColors.TextMuted,
    outline = QuantumColors.NavyOutline,
    error = Color(0xFFFF6B7A)
)

/** Dark-only by design: the identity is a deep navy background. */
@Composable
fun QuantumTheme(content: @Composable () -> Unit) = MaterialTheme(colorScheme = Scheme, content = content)

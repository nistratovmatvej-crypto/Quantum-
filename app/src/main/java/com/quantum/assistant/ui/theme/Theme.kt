package com.quantum.assistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Quantum's own identity: deep Atlantic teal, warm porcelain and a precise cyan.
 * The palette is inspired by the restraint and material clarity of recent iOS,
 * without copying Apple's interface or brand assets.
 */
object QuantumColors {
    val Background = Color(0xFF071B22)
    val BackgroundElevated = Color(0xFF0B252E)
    val Surface = Color(0xFF102F39)
    val SurfaceRaised = Color(0xFF173B46)
    val Outline = Color(0xFF28515D)
    val Accent = Color(0xFF08A8C9)
    val AccentBright = Color(0xFF36C9E5)
    val Porcelain = Color(0xFFF4F3EF)
    val TextMuted = Color(0xFFA4BAC0)
    val Success = Color(0xFF70D6B1)
    val Warning = Color(0xFFFFC477)

    // Compatibility aliases used by the existing Alpha foundation.
    val Navy = Background
    val NavySurface = Surface
    val NavyOutline = Outline
    val Cyan = AccentBright
    val StarWhite = Porcelain
}

private val Scheme = darkColorScheme(
    primary = QuantumColors.AccentBright,
    onPrimary = QuantumColors.Background,
    secondary = QuantumColors.Accent,
    onSecondary = QuantumColors.Porcelain,
    background = QuantumColors.Background,
    onBackground = QuantumColors.Porcelain,
    surface = QuantumColors.Surface,
    onSurface = QuantumColors.Porcelain,
    surfaceVariant = QuantumColors.SurfaceRaised,
    onSurfaceVariant = QuantumColors.TextMuted,
    outline = QuantumColors.Outline,
    error = Color(0xFFFF7B82)
)

@Composable
fun QuantumTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, content = content)
}

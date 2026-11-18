package com.quantum.assistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Quantum Atlantic — restrained cyan, warm porcelain and deep ink. */
object QuantumColors {
    val Background = Color(0xFF071522)
    val BackgroundElevated = Color(0xFF0B1E2D)
    val Surface = Color(0xFF102638)
    val SurfaceRaised = Color(0xFF163449)
    val SurfaceSoft = Color(0xFF0D2435)
    val Outline = Color(0xFF244354)
    val Accent = Color(0xFF079FC0)
    val AccentDeep = Color(0xFF078EAE)
    val AccentBright = Color(0xFF35BBD5)
    val Porcelain = Color(0xFFF4F3EF)
    val TextMuted = Color(0xFF91A8B5)
    val Success = Color(0xFF72D6B2)
    val Warning = Color(0xFFFFC477)
    val Danger = Color(0xFFFF7D86)
    val Navy = Background
    val NavySurface = Surface
    val NavyOutline = Outline
    val Cyan = AccentBright
    val StarWhite = Porcelain
}
private val Scheme = darkColorScheme(
    primary = QuantumColors.AccentBright, onPrimary = QuantumColors.Background,
    secondary = QuantumColors.Accent, onSecondary = QuantumColors.Porcelain,
    background = QuantumColors.Background, onBackground = QuantumColors.Porcelain,
    surface = QuantumColors.Surface, onSurface = QuantumColors.Porcelain,
    surfaceVariant = QuantumColors.SurfaceRaised, onSurfaceVariant = QuantumColors.TextMuted,
    outline = QuantumColors.Outline, error = QuantumColors.Danger
)
private val QuantumTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontSize = 42.sp, lineHeight = 48.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-1.4).sp),
        headlineLarge = headlineLarge.copy(fontSize = 32.sp, lineHeight = 38.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.7).sp),
        headlineMedium = headlineMedium.copy(fontSize = 27.sp, lineHeight = 33.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.4).sp),
        titleLarge = titleLarge.copy(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold),
        titleMedium = titleMedium.copy(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.Medium),
        bodyLarge = bodyLarge.copy(fontSize = 16.sp, lineHeight = 24.sp),
        bodyMedium = bodyMedium.copy(fontSize = 14.sp, lineHeight = 21.sp),
        bodySmall = bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
        labelLarge = labelLarge.copy(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold),
        labelMedium = labelMedium.copy(fontSize = 11.sp, lineHeight = 15.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
    )
}
@Composable
fun QuantumTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Scheme, typography = QuantumTypography,
        shapes = Shapes(
            extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
            small = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            large = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
            extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(34.dp)
        ), content = content
    )
}

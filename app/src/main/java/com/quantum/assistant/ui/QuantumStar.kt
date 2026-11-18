package com.quantum.assistant.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.quantum.assistant.ui.theme.QuantumColors

/** Concentric Quantum mark with a restrained breathing halo during active states. */
@Composable
fun QuantumStar(modifier: Modifier = Modifier, glow: Boolean = false) {
    val transition = rememberInfiniteTransition(label = "quantum-mark")
    val haloAlpha by transition.animateFloat(
        initialValue = if (glow) .16f else .10f,
        targetValue = if (glow) .34f else .10f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (glow) 1450 else 1, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo-alpha"
    )
    Canvas(modifier) {
        val c = center
        val r = size.minDimension * .46f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(QuantumColors.Accent.copy(alpha = haloAlpha), Color.Transparent),
                center = c, radius = r * 1.65f
            ), radius = r * 1.65f, center = c
        )
        drawCircle(QuantumColors.Porcelain, r, c)
        drawCircle(
            brush = Brush.linearGradient(
                listOf(QuantumColors.AccentBright, QuantumColors.AccentDeep),
                start = Offset(c.x - r, c.y - r), end = Offset(c.x + r, c.y + r)
            ), r * .62f, c
        )
        drawCircle(QuantumColors.Porcelain, r * .19f, c)
        drawCircle(Color.White.copy(alpha = .18f), r * .985f, c,
            style = Stroke(width = (r * .012f).coerceAtLeast(1f)))
    }
}

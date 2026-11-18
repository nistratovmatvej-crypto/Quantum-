package com.quantum.assistant.ui

import androidx.compose.foundation.Canvas
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.quantum.assistant.ui.theme.QuantumColors

/** Geometric concentric Quantum mark. Drawn natively for crisp adaptive sizing. */
@Composable
fun QuantumStar(modifier: Modifier = Modifier, glow: Boolean = false) {
    Canvas(modifier) {
        val c = center
        val r = size.minDimension * .46f
        if (glow) drawCircle(
            brush = Brush.radialGradient(
                listOf(QuantumColors.Accent.copy(alpha = .22f), Color.Transparent),
                center = c, radius = r * 1.55f
            ), radius = r * 1.55f, center = c
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

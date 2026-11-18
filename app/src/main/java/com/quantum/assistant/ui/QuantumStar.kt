package com.quantum.assistant.ui

import androidx.compose.foundation.Canvas
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.quantum.assistant.ui.theme.QuantumColors

/**
 * Quantum's concentric-orbit mark, adapted from the user's supplied logo.
 * Kept as a Canvas drawing so it is crisp at every size and needs no image asset.
 */
@Composable
fun QuantumStar(modifier: Modifier = Modifier, glow: Boolean = false) {
    Canvas(modifier) {
        val c = center
        val radius = size.minDimension * 0.46f
        if (glow) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(QuantumColors.Accent.copy(alpha = 0.30f), Color.Transparent),
                    center = c,
                    radius = radius * 1.45f
                ),
                radius = radius * 1.45f,
                center = c
            )
        }
        // Outer porcelain ring.
        drawCircle(color = QuantumColors.Porcelain, radius = radius, center = c)
        // Cyan inner field leaves a consistent, broad ring.
        drawCircle(color = QuantumColors.Accent, radius = radius * 0.68f, center = c)
        // Center dot echoes the supplied mark.
        drawCircle(color = QuantumColors.Porcelain, radius = radius * 0.18f, center = c)
        // A fine highlight gives the mark a little depth without a 3D effect.
        drawCircle(
            color = Color.White.copy(alpha = 0.14f),
            radius = radius * 0.985f,
            center = Offset(c.x, c.y - radius * 0.015f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = radius * 0.012f)
        )
    }
}

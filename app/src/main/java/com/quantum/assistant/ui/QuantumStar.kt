package com.quantum.assistant.ui

import androidx.compose.foundation.Canvas
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.quantum.assistant.ui.theme.QuantumColors
import kotlin.math.cos
import kotlin.math.sin

/** White five-point star, cool gradient, restrained highlight, optional soft cyan glow. */
@Composable
fun QuantumStar(modifier: Modifier = Modifier, glow: Boolean = false) {
    Canvas(modifier) {
        val c = center
        val half = size.minDimension / 2f
        val outer = half * 0.78f
        val inner = outer * 0.45f
        val path = Path().apply {
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) outer else inner
                val a = Math.toRadians(-90.0 + i * 36.0)
                val x = c.x + (r * cos(a)).toFloat()
                val y = c.y + (r * sin(a)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        if (glow) drawCircle(
            brush = Brush.radialGradient(listOf(QuantumColors.Cyan.copy(alpha = 0.28f), Color.Transparent), center = c, radius = half),
            radius = half, center = c
        )
        val fill = Brush.linearGradient(
            listOf(Color.White, Color(0xFFD6F3FF), Color(0xFF9FE4F6)),
            start = Offset(c.x - outer, c.y - outer), end = Offset(c.x + outer, c.y + outer)
        )
        drawPath(path, fill)
        // Rounded corners + subtle gloss edge.
        drawPath(path, Color.White.copy(alpha = 0.7f), style = Stroke(width = half * 0.06f, join = StrokeJoin.Round))
    }
}

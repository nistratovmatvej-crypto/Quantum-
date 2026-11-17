package com.quantum.assistant.floating

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.quantum.assistant.ui.QuantumStar
import com.quantum.assistant.ui.theme.QuantumColors

/** Everything the panel can ask the state machine to do. */
class PanelActions(
    val onStarTap: () -> Unit, val onMicTap: () -> Unit, val onStopListening: () -> Unit, val onClose: () -> Unit,
    val onApprove: () -> Unit, val onReject: () -> Unit, val onSpeakingFinished: () -> Unit, val onDismissError: () -> Unit
) {
    companion object {
        fun from(c: PanelController) = PanelActions(
            { c.onStarTap() }, { c.onMicTap() }, { c.onStopListening() }, { c.onClose() },
            { c.onApprove(speak = false) }, { c.onReject() }, { c.onSpeakingFinished() }, { c.onDismissError() }
        )
    }
}

/**
 * In-app floating star + horizontal panel. NOT a system overlay yet (see CLAUDE_HANDOFF.md).
 * Tap 1 expands; tap 2 (the mic control) starts listening; close is always visible when expanded.
 */
@Composable
fun FloatingAssistant(
    state: PanelState,
    secondsLeft: Int,
    captureLive: Boolean,
    timeoutIsSilenceDetection: Boolean,
    errorMessage: String?,
    actions: PanelActions,
    modifier: Modifier = Modifier
) {
    val expanded = state != PanelState.COLLAPSED
    val listening = state == PanelState.LISTENING
    val shape = RoundedCornerShape(36.dp)

    Row(
        modifier = modifier
            .animateContentSize(spring(dampingRatio = 0.85f, stiffness = 400f))
            .background(QuantumColors.NavySurface, shape)
            .border(1.dp, if (listening) QuantumColors.Cyan else QuantumColors.NavyOutline, shape)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            Modifier.size(64.dp).clickable(enabled = !expanded, role = Role.Button, onClick = actions.onStarTap)
                .semantics { contentDescription = if (expanded) "Quantum" else "Open Quantum panel" },
            contentAlignment = Alignment.Center
        ) { QuantumStar(Modifier.size(52.dp), glow = !expanded) }

        if (expanded) {
            val (title, detail) = when (state) {
                PanelState.EXPANDED_IDLE -> "Microphone off" to
                    if (captureLive) "Tap the mic to start." else "Capture unavailable in this build. Mic gives a preview only."
                PanelState.LISTENING -> when {
                    !captureLive -> "Preview only" to "No audio is captured. Collapses in ${secondsLeft}s."
                    timeoutIsSilenceDetection -> "Listening" to "Stops after ${secondsLeft}s without speech."
                    else -> "Listening" to "Tap stop when you are done."
                }
                PanelState.PROCESSING -> "Working" to "Please wait."
                PanelState.AWAITING_CONFIRMATION -> "Confirm action" to "Nothing runs until you approve."
                PanelState.SPEAKING -> "Speaking" to ""
                PanelState.ERROR -> "Something went wrong" to (errorMessage ?: "")
                PanelState.COLLAPSED -> "" to ""
            }
            Column(Modifier.widthIn(min = 120.dp, max = 190.dp).padding(horizontal = 4.dp).semantics { liveRegion = LiveRegionMode.Polite }) {
                Text(title, style = MaterialTheme.typography.labelLarge, color = if (listening) QuantumColors.Cyan else Color.White)
                if (detail.isNotEmpty()) Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                when (state) {
                    PanelState.AWAITING_CONFIRMATION -> Row {
                        TextButton(onClick = actions.onApprove) { Text("Approve") }
                        TextButton(onClick = actions.onReject) { Text("Reject") }
                    }
                    PanelState.SPEAKING -> TextButton(onClick = actions.onSpeakingFinished) { Text("Stop") }
                    PanelState.ERROR -> TextButton(onClick = actions.onDismissError) { Text("Dismiss") }
                    else -> Unit
                }
            }
            if (state == PanelState.EXPANDED_IDLE || listening) {
                MicButton(listening, if (listening) actions.onStopListening else actions.onMicTap)
            }
            Box(
                Modifier.size(48.dp).clickable(role = Role.Button, onClick = actions.onClose).semantics { contentDescription = "Close panel" },
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
private fun MicButton(listening: Boolean, onClick: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f, targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse"
    )
    Box(
        Modifier.size(56.dp).clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = if (listening) "Stop listening" else "Start microphone" },
        contentAlignment = Alignment.Center
    ) {
        if (listening) Box(Modifier.size(44.dp).scale(pulse).background(QuantumColors.Cyan.copy(alpha = 0.25f), CircleShape))
        Box(
            Modifier.size(44.dp).background(if (listening) QuantumColors.Cyan else QuantumColors.NavyOutline, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val ink = if (listening) QuantumColors.Navy else Color.White
            Canvas(Modifier.size(22.dp)) {
                if (listening) {
                    drawRoundRect(ink, Offset(size.width * 0.2f, size.height * 0.2f), Size(size.width * 0.6f, size.height * 0.6f), CornerRadius(4f))
                } else {
                    val w = size.width
                    drawRoundRect(ink, Offset(w * 0.35f, 0f), Size(w * 0.3f, w * 0.55f), CornerRadius(w * 0.15f))
                    drawArc(ink, 0f, 180f, false, Offset(w * 0.2f, w * 0.2f), Size(w * 0.6f, w * 0.5f), style = Stroke(2.5f, cap = StrokeCap.Round))
                    drawLine(ink, Offset(w * 0.5f, w * 0.7f), Offset(w * 0.5f, w * 0.95f), 2.5f, StrokeCap.Round)
                }
            }
        }
    }
}

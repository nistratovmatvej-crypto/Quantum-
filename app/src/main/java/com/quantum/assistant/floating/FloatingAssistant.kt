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

class PanelActions(
    val onStarTap: () -> Unit,
    val onMicTap: () -> Unit,
    val onStopListening: () -> Unit,
    val onClose: () -> Unit,
    val onApprove: () -> Unit,
    val onReject: () -> Unit,
    val onSpeakingFinished: () -> Unit,
    val onDismissError: () -> Unit
) {
    companion object {
        fun from(c: PanelController, onMicTap: () -> Unit = { c.onMicTap() }) = PanelActions(
            { c.onStarTap() }, onMicTap, { c.onStopListening() }, { c.onClose() },
            { c.onApprove(speak = false) }, { c.onReject() }, { c.onSpeakingFinished() }, { c.onDismissError() }
        )
    }
}

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
    val shape = RoundedCornerShape(34.dp)

    Row(
        modifier = modifier.animateContentSize(spring(dampingRatio = 0.85f, stiffness = 400f))
            .background(QuantumColors.BackgroundElevated, shape)
            .border(1.dp, if (listening) QuantumColors.AccentBright else QuantumColors.Outline, shape)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            Modifier.size(60.dp).clickable(enabled = !expanded, role = Role.Button, onClick = actions.onStarTap)
                .semantics { contentDescription = if (expanded) "Quantum" else "Открыть Quantum" },
            contentAlignment = Alignment.Center
        ) { QuantumStar(Modifier.size(48.dp), glow = !expanded) }

        if (expanded) {
            val (title, detail) = when (state) {
                PanelState.EXPANDED_IDLE -> "Готов к работе" to "Нажми на микрофон, чтобы начать."
                PanelState.LISTENING -> if (timeoutIsSilenceDetection) {
                    "Слушаю тебя" to "Остановка при тишине: $secondsLeft сек."
                } else "Слушаю" to "Нажми стоп, когда закончишь."
                PanelState.PROCESSING -> "Обрабатываю" to "Распознаю речь."
                PanelState.AWAITING_CONFIRMATION -> "Нужно подтверждение" to "Ничего не выполнится без тебя."
                PanelState.SPEAKING -> "Говорю" to ""
                PanelState.ERROR -> "Не удалось запустить" to (errorMessage ?: "Проверь разрешение микрофона.")
                PanelState.COLLAPSED -> "" to ""
            }
            Column(
                Modifier.widthIn(min = 126.dp, max = 196.dp).padding(horizontal = 4.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            ) {
                Text(title, style = MaterialTheme.typography.labelLarge, color = if (listening) QuantumColors.AccentBright else QuantumColors.Porcelain)
                if (detail.isNotEmpty()) Text(detail, style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
                when (state) {
                    PanelState.AWAITING_CONFIRMATION -> Row {
                        TextButton(onClick = actions.onApprove) { Text("Да") }
                        TextButton(onClick = actions.onReject) { Text("Нет") }
                    }
                    PanelState.SPEAKING -> TextButton(onClick = actions.onSpeakingFinished) { Text("Стоп") }
                    PanelState.ERROR -> TextButton(onClick = actions.onDismissError) { Text("Понятно") }
                    else -> Unit
                }
            }
            if (state == PanelState.EXPANDED_IDLE || listening) {
                MicButton(listening, if (listening) actions.onStopListening else actions.onMicTap)
            }
            Box(
                Modifier.size(46.dp).clickable(role = Role.Button, onClick = actions.onClose)
                    .semantics { contentDescription = "Закрыть панель" },
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Close, null, tint = QuantumColors.TextMuted) }
        }
    }
}

@Composable
private fun MicButton(listening: Boolean, onClick: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f, targetValue = 1.22f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    Box(
        Modifier.size(54.dp).clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = if (listening) "Остановить запись" else "Включить микрофон" },
        contentAlignment = Alignment.Center
    ) {
        if (listening) Box(Modifier.size(42.dp).scale(pulse).background(QuantumColors.Accent.copy(alpha = 0.25f), CircleShape))
        Box(
            Modifier.size(42.dp).background(if (listening) QuantumColors.AccentBright else QuantumColors.SurfaceRaised, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val ink = if (listening) QuantumColors.Background else QuantumColors.Porcelain
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

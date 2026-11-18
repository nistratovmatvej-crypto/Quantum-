package com.quantum.assistant.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quantum.assistant.data.AssistantSettings
import com.quantum.assistant.floating.FloatingAssistant
import com.quantum.assistant.floating.PanelActions
import com.quantum.assistant.floating.PanelState
import com.quantum.assistant.ui.QuantumStar
import com.quantum.assistant.ui.theme.QuantumColors

@Composable
fun HomeScreen(vm: HomeViewModel, settings: AssistantSettings, onOpenSettings: () -> Unit) {
    val state by vm.panel.state.collectAsState()
    val seconds by vm.panel.secondsLeft.collectAsState()
    val errorMessage by vm.panel.errorMessage.collectAsState()
    val live = vm.panel.isCaptureLive
    val micLine = when {
        state == PanelState.LISTENING && live -> "Listening"
        state == PanelState.LISTENING -> "Preview only · no audio"
        else -> "Microphone is off"
    }

    Box(Modifier.fillMaxSize().background(QuantumColors.Background).safeDrawingPadding()) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 26.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantumStar(Modifier.size(34.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text("QUANTUM", style = MaterialTheme.typography.labelLarge, color = QuantumColors.AccentBright, fontWeight = FontWeight.Bold)
                    Text(settings.displayName, style = MaterialTheme.typography.titleMedium, color = QuantumColors.Porcelain, fontWeight = FontWeight.SemiBold)
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = QuantumColors.Porcelain)
                }
            }

            Text(
                "Your assistant.",
                style = MaterialTheme.typography.headlineLarge,
                color = QuantumColors.Porcelain,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "A calmer way to get things done.",
                style = MaterialTheme.typography.bodyLarge,
                color = QuantumColors.TextMuted,
                modifier = Modifier.padding(top = 5.dp)
            )

            Card(
                Modifier.fillMaxWidth().padding(top = 26.dp),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = QuantumColors.BackgroundElevated)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(color = QuantumColors.Surface, shape = CircleShape) {
                        Box(Modifier.size(196.dp), contentAlignment = Alignment.Center) {
                            QuantumStar(Modifier.size(168.dp), glow = true)
                        }
                    }
                    Text(
                        when (state) {
                            PanelState.LISTENING -> "I'm listening"
                            PanelState.PROCESSING -> "Working on it"
                            PanelState.SPEAKING -> "Speaking"
                            PanelState.AWAITING_CONFIRMATION -> "Needs your approval"
                            PanelState.ERROR -> "Something went wrong"
                            else -> "Ready when you are"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = QuantumColors.Porcelain,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Text(
                        "Tap the Quantum button to open the assistant controls.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = QuantumColors.TextMuted,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(top = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SYSTEM STATUS", style = MaterialTheme.typography.labelMedium, color = QuantumColors.TextMuted, fontWeight = FontWeight.Bold)
                Surface(color = QuantumColors.Surface, shape = RoundedCornerShape(50)) {
                    Text("ALPHA 0.1.1", modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, color = QuantumColors.AccentBright)
                }
            }
            Column(Modifier.padding(top = 12.dp, bottom = 132.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatusRow("Microphone", micLine, active = state == PanelState.LISTENING && live)
                StatusRow("Cloud intelligence", "Not connected")
                StatusRow("On-device model", "No model installed")
                StatusRow("Device control", "Not implemented")
            }
        }

        FloatingAssistant(
            state = state,
            secondsLeft = seconds,
            captureLive = live,
            timeoutIsSilenceDetection = vm.panel.timeoutIsSilenceDetection,
            errorMessage = errorMessage,
            actions = remember(vm) { PanelActions.from(vm.panel) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }
}

@Composable
private fun StatusRow(label: String, value: String, active: Boolean = false) {
    Row(
        Modifier.fillMaxWidth()
            .background(QuantumColors.Surface, RoundedCornerShape(20.dp))
            .border(1.dp, if (active) QuantumColors.Accent else QuantumColors.Outline.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).background(if (active) QuantumColors.Success else QuantumColors.TextMuted.copy(alpha = 0.55f), CircleShape))
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = QuantumColors.Porcelain, fontWeight = FontWeight.Medium)
            Text(value, style = MaterialTheme.typography.bodySmall, color = if (active) QuantumColors.Success else QuantumColors.TextMuted)
        }
    }
}

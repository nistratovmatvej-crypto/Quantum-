package com.quantum.assistant.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
    val live = vm.panel.isCaptureLive
    val micLine = when {
        state == PanelState.LISTENING && live -> "Listening"
        state == PanelState.LISTENING -> "Preview only, no audio"
        live -> "Off"
        else -> "Off (capture unavailable in this build)"
    }

    Box(Modifier.fillMaxSize().safeDrawingPadding()) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(settings.displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Settings, contentDescription = "Settings") }
            }
            Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                QuantumStar(Modifier.size(180.dp), glow = true)
            }
            Text(
                "Alpha 0.1 foundation. The assistant is not connected to any AI yet, so it cannot answer questions or control the device.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(Modifier.padding(top = 20.dp, bottom = 120.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatusRow("Microphone", micLine, active = state == PanelState.LISTENING && live)
                StatusRow("Cloud AI", "Not connected")
                StatusRow("Local AI", "No model installed")
                StatusRow("Device actions", "Not implemented")
            }
        }
        val errorMessage by vm.panel.errorMessage.collectAsState()
        FloatingAssistant(
            state = state, secondsLeft = seconds,
            captureLive = live, timeoutIsSilenceDetection = vm.panel.timeoutIsSilenceDetection,
            errorMessage = errorMessage, actions = remember(vm) { PanelActions.from(vm.panel) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }
}

@Composable
private fun StatusRow(label: String, value: String, active: Boolean = false) {
    Row(
        Modifier.fillMaxWidth()
            .background(QuantumColors.NavySurface, RoundedCornerShape(16.dp))
            .border(1.dp, if (active) QuantumColors.Cyan else QuantumColors.NavyOutline, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = if (active) QuantumColors.Cyan else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

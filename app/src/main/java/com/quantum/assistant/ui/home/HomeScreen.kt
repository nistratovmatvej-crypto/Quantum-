package com.quantum.assistant.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.quantum.assistant.data.AssistantSettings
import com.quantum.assistant.floating.FloatingAssistant
import com.quantum.assistant.floating.PanelActions
import com.quantum.assistant.floating.PanelState
import com.quantum.assistant.ui.QuantumStar
import com.quantum.assistant.ui.theme.QuantumColors

@Composable
fun HomeScreen(
    vm: HomeViewModel,
    settings: AssistantSettings,
    profileName: String?,
    onOpenSettings: () -> Unit,
    onOpenPlans: () -> Unit
) {
    val context = LocalContext.current
    val state by vm.panel.state.collectAsState()
    val seconds by vm.panel.secondsLeft.collectAsState()
    val errorMessage by vm.panel.errorMessage.collectAsState()
    val transcript by vm.panel.transcript.collectAsState()
    val live = vm.panel.isCaptureLive

    val micPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) vm.panel.onMicTap()
        else vm.panel.onFailure("Без разрешения Android не может использовать микрофон.")
    }
    val actions = remember(vm, micPermission) {
        PanelActions.from(vm.panel) {
            val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            if (granted) vm.panel.onMicTap() else micPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Box(Modifier.fillMaxSize().background(QuantumColors.Background).safeDrawingPadding()) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp).padding(top = 76.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 26.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantumStar(Modifier.size(34.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text("QUANTUM", style = MaterialTheme.typography.labelLarge, color = QuantumColors.AccentBright, fontWeight = FontWeight.Bold)
                    Text(profileName ?: settings.displayName, style = MaterialTheme.typography.titleMedium, color = QuantumColors.Porcelain, fontWeight = FontWeight.SemiBold)
                }
                Button(onClick = onOpenPlans) { Text("Уровни") }
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Настройки", tint = QuantumColors.Porcelain)
                }
            }

            Text("Твой помощник.", style = MaterialTheme.typography.headlineLarge, color = QuantumColors.Porcelain, fontWeight = FontWeight.SemiBold)
            Text("Готов помочь, когда понадобится.", style = MaterialTheme.typography.bodyLarge, color = QuantumColors.TextMuted, modifier = Modifier.padding(top = 5.dp))

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
                            PanelState.LISTENING -> "Я слушаю"
                            PanelState.PROCESSING -> "Распознаю речь"
                            PanelState.SPEAKING -> "Говорю"
                            PanelState.AWAITING_CONFIRMATION -> "Жду подтверждения"
                            PanelState.ERROR -> "Нужна проверка"
                            else -> "Готов к работе"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = QuantumColors.Porcelain,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Text(
                        "Нажми на плавающий значок Quantum, чтобы открыть микрофон.",
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
                Text("СОСТОЯНИЕ СИСТЕМЫ", style = MaterialTheme.typography.labelMedium, color = QuantumColors.TextMuted, fontWeight = FontWeight.Bold)
                Surface(color = QuantumColors.Surface, shape = RoundedCornerShape(50)) {
                    Text("ALPHA 0.2", modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, color = QuantumColors.AccentBright)
                }
            }
            Column(Modifier.padding(top = 12.dp, bottom = 132.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatusRow("Микрофон", if (state == PanelState.LISTENING && live) "Активен" else "Выключен", active = state == PanelState.LISTENING && live)
                StatusRow("Распознавание речи", if (live) "Служба доступна" else "Недоступно на устройстве", active = live)
                StatusRow("Облачный ИИ", "Не подключён")
                StatusRow("Локальная модель", "Не установлена")
                if (!transcript.isNullOrBlank()) {
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("ПОСЛЕДНЯЯ РАСПОЗНАННАЯ ФРАЗА", color = QuantumColors.AccentBright, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text(transcript!!, color = QuantumColors.Porcelain, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 6.dp))
                            Text("Текст распознан Android. Ответы ИИ пока не подключены.", color = QuantumColors.TextMuted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }
        }

        FloatingAssistant(
            state = state,
            secondsLeft = seconds,
            captureLive = live,
            timeoutIsSilenceDetection = vm.panel.timeoutIsSilenceDetection,
            errorMessage = errorMessage,
            actions = actions,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)
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

package com.quantum.assistant.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
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
import androidx.compose.ui.graphics.Brush
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
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp).padding(top = 82.dp, bottom = 140.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                QuantumStar(Modifier.size(34.dp))
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text("QUANTUM", style = MaterialTheme.typography.labelLarge, color = QuantumColors.AccentBright, fontWeight = FontWeight.Bold)
                    Text(profileName ?: settings.displayName, style = MaterialTheme.typography.titleMedium, color = QuantumColors.Porcelain)
                }
                IconButton(onClick = onOpenPlans) {
                    Icon(Icons.Outlined.Tune, contentDescription = "Модели и тариф", tint = QuantumColors.TextMuted)
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Настройки", tint = QuantumColors.Porcelain)
                }
            }

            Text(
                "Технологии,\nкоторые слушают.",
                style = MaterialTheme.typography.headlineLarge,
                color = QuantumColors.Porcelain,
                modifier = Modifier.padding(top = 38.dp)
            )
            Text(
                "Твой личный помощник — в одном касании.",
                style = MaterialTheme.typography.bodyLarge,
                color = QuantumColors.TextMuted,
                modifier = Modifier.padding(top = 10.dp)
            )

            Card(
                Modifier.fillMaxWidth().padding(top = 30.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = QuantumColors.BackgroundElevated)
            ) {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(QuantumColors.SurfaceSoft, QuantumColors.BackgroundElevated)))
                        .padding(horizontal = 22.dp, vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(190.dp),
                            shape = CircleShape,
                            color = QuantumColors.Surface,
                            tonalElevation = 0.dp,
                            shadowElevation = 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                QuantumStar(Modifier.size(146.dp), glow = state == PanelState.LISTENING)
                            }
                        }
                        AnimatedContent(
                            targetState = state,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "assistant-state"
                        ) { current ->
                            Text(
                                when (current) {
                                    PanelState.LISTENING -> "Я слушаю"
                                    PanelState.PROCESSING -> "Обрабатываю речь"
                                    PanelState.SPEAKING -> "Говорю"
                                    PanelState.AWAITING_CONFIRMATION -> "Жду подтверждения"
                                    PanelState.ERROR -> "Не удалось выполнить"
                                    else -> "Я готов"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = QuantumColors.Porcelain,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }
                        Text(
                            when (state) {
                                PanelState.LISTENING -> if (vm.panel.timeoutIsSilenceDetection) "Автоостановка при тишине · $seconds сек." else "Нажми на стоп, когда закончишь."
                                PanelState.PROCESSING -> "Распознаю сказанное устройством."
                                PanelState.ERROR -> errorMessage ?: "Проверь разрешение микрофона."
                                else -> "Коснись панели Quantum сверху, чтобы начать."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = QuantumColors.TextMuted,
                            modifier = Modifier.padding(top = 7.dp)
                        )
                        Surface(
                            modifier = Modifier.padding(top = 18.dp),
                            shape = RoundedCornerShape(50.dp),
                            color = QuantumColors.SurfaceRaised
                        ) {
                            Row(
                                Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier.size(7.dp).background(
                                        if (state == PanelState.LISTENING && live) QuantumColors.Success else QuantumColors.TextMuted,
                                        CircleShape
                                    )
                                )
                                Text(
                                    if (state == PanelState.LISTENING && live) "МИКРОФОН АКТИВЕН" else "ОЖИДАНИЕ КОМАНДЫ",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (state == PanelState.LISTENING && live) QuantumColors.Success else QuantumColors.TextMuted,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(top = 30.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("СОСТОЯНИЕ", style = MaterialTheme.typography.labelMedium, color = QuantumColors.TextMuted)
                Spacer(Modifier.weight(1f))
                Text("ALPHA 0.2", style = MaterialTheme.typography.labelMedium, color = QuantumColors.AccentBright)
            }
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                StatusLine("Распознавание речи", if (live) "Активно" else "Ожидает запуска", live)
                StatusLine("Облачный интеллект", "Не подключён", false)
                StatusLine("Локальная модель", "Не установлена", false)
            }

            if (!transcript.isNullOrBlank()) {
                Card(
                    Modifier.fillMaxWidth().padding(top = 22.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text("ПОСЛЕДНЯЯ ФРАЗА", style = MaterialTheme.typography.labelMedium, color = QuantumColors.AccentBright)
                        Text(transcript!!, style = MaterialTheme.typography.bodyLarge, color = QuantumColors.Porcelain, modifier = Modifier.padding(top = 9.dp))
                        Text("Это распознанный текст. Ответы ИИ пока не подключены.", style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
            Text(
                "Quantum работает только после твоего действия. Фоновое прослушивание выключено.",
                style = MaterialTheme.typography.bodySmall,
                color = QuantumColors.TextMuted,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )
        }

        FloatingAssistant(
            state = state, secondsLeft = seconds, captureLive = live,
            timeoutIsSilenceDetection = vm.panel.timeoutIsSilenceDetection,
            errorMessage = errorMessage, actions = actions,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)
        )
    }
}

@Composable
private fun StatusLine(label: String, value: String, active: Boolean) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(7.dp).background(if (active) QuantumColors.Success else QuantumColors.Outline, CircleShape))
        Text(label, Modifier.weight(1f).padding(start = 12.dp), style = MaterialTheme.typography.bodyMedium, color = QuantumColors.Porcelain)
        Text(value, style = MaterialTheme.typography.bodySmall, color = if (active) QuantumColors.Success else QuantumColors.TextMuted)
    }
}

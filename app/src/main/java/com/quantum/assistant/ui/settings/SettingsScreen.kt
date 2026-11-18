package com.quantum.assistant.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quantum.assistant.BuildConfig
import com.quantum.assistant.data.AssistantSettings
import com.quantum.assistant.data.MemoryMode
import com.quantum.assistant.data.SettingsRepository
import com.quantum.assistant.device.DeviceSnapshotReader
import com.quantum.assistant.device.ThresholdCapabilityAssessor
import com.quantum.assistant.ui.QuantumStar
import com.quantum.assistant.ui.theme.QuantumColors
import java.util.Locale

@Composable
fun SettingsScreen(repo: SettingsRepository, settings: AssistantSettings, onBack: () -> Unit) {
    val context = LocalContext.current
    val device = remember { DeviceSnapshotReader.read(context) }
    val assessment = remember(device) { ThresholdCapabilityAssessor().assess(device) }

    Column(
        Modifier.fillMaxSize().background(QuantumColors.Background).safeDrawingPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = QuantumColors.Porcelain)
            }
            Column(Modifier.weight(1f)) {
                Text("Настройки", style = MaterialTheme.typography.headlineMedium, color = QuantumColors.Porcelain)
                Text("Настрой Quantum под себя", style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
            }
            QuantumStar(Modifier.size(30.dp))
        }

        SettingsGroup("ПРОФИЛЬ", Icons.Outlined.Person) {
            Text("Имя помощника", style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
            OutlinedTextField(
                value = settings.assistantName, onValueChange = repo::setAssistantName,
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Quantum") }
            )
            Text("Профиль хранится только на этом устройстве. Сетевой аккаунт не подключён.", style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
        }

        SettingsGroup("ГОЛОС", Icons.Outlined.RecordVoiceOver) {
            Text("Скорость речи · " + String.format(Locale.US, "%.1fx", settings.speechSpeed), style = MaterialTheme.typography.bodyLarge, color = QuantumColors.Porcelain)
            Slider(value = settings.speechSpeed, onValueChange = { repo.setSpeechSpeed(Math.round(it * 10) / 10f) }, valueRange = 0.5f..2.0f, steps = 14)
            Text("Настройка сохранена. Применение станет доступно после подключения синтеза речи.", style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
        }

        SettingsGroup("ПАМЯТЬ", Icons.Outlined.Memory) {
            MemoryMode.entries.forEach { mode ->
                Row(
                    Modifier.fillMaxWidth().clickable(role = Role.RadioButton) { repo.setMemoryMode(mode) }.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = settings.memoryMode == mode, onClick = null)
                    Column(Modifier.padding(start = 10.dp)) {
                        Text(mode.label, color = QuantumColors.Porcelain, fontWeight = FontWeight.Medium)
                        Text(mode.description, style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
                    }
                }
            }
            Text("Сейчас сохраняется только выбор режима. Сама память ещё не подключена.", style = MaterialTheme.typography.bodySmall, color = QuantumColors.Warning)
        }

        SettingsGroup("ИНТЕЛЛЕКТ НА УСТРОЙСТВЕ", Icons.Outlined.Tune) {
            Text("Предпочитаемый уровень", style = MaterialTheme.typography.bodyLarge, color = QuantumColors.Porcelain)
            TierRow("Автоматически", "Рекомендация: ${assessment.recommended?.label ?: "нет данных"}", settings.selectedTier == null) { repo.setSelectedTier(null) }
            assessment.tiers.forEach { tier ->
                val detail = tier.verdict.label + if (tier.reasons.isEmpty()) "" else " · " + tier.reasons.joinToString("; ")
                TierRow(tier.tier.label, detail, settings.selectedTier == tier.tier) { repo.setSelectedTier(tier.tier) }
            }
            Text(
                "RAM: ${device.totalRamMb} MB · Android API ${device.sdkInt}. Уровни пока являются профилями выбора: модели не установлены.",
                style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted
            )
        }

        SettingsGroup("КОНФИДЕНЦИАЛЬНОСТЬ", Icons.Outlined.Lock) {
            Text("Микрофон не работает в фоне. Разрешение запрашивается только при нажатии на кнопку микрофона.", style = MaterialTheme.typography.bodyMedium, color = QuantumColors.Porcelain)
            Text(
                "Управление разрешениями Android",
                Modifier.fillMaxWidth().clickable {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)))
                }.padding(vertical = 12.dp),
                color = QuantumColors.AccentBright, fontWeight = FontWeight.SemiBold
            )
        }

        SettingsGroup("О QUANTUM", null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuantumStar(Modifier.size(42.dp))
                Column(Modifier.padding(start = 12.dp)) {
                    Text("Quantum", style = MaterialTheme.typography.titleLarge, color = QuantumColors.Porcelain)
                    Text("Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
                }
            }
            Text("Cloud AI, локальные модели, TTS, напоминания и действия устройства пока не доступны.", style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
        }
        Text("Quantum Atlantic · Alpha", style = MaterialTheme.typography.labelSmall, color = QuantumColors.TextMuted, modifier = Modifier.padding(bottom = 26.dp))
    }
}

@Composable
private fun SettingsGroup(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector?, content: @Composable () -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) Icon(icon, contentDescription = null, tint = QuantumColors.AccentBright, modifier = Modifier.size(18.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, color = QuantumColors.AccentBright, modifier = Modifier.padding(start = if (icon == null) 0.dp else 9.dp))
            }
            content()
        }
    }
}

@Composable
private fun TierRow(title: String, detail: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.RadioButton, onClick = onSelect).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Column(Modifier.padding(start = 10.dp)) {
            Text(title, color = QuantumColors.Porcelain)
            Text(detail, style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
        }
    }
}

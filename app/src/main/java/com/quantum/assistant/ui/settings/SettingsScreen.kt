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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
            .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = QuantumColors.Porcelain)
            }
            Column(Modifier.weight(1f)) {
                Text("Settings", style = MaterialTheme.typography.headlineSmall, color = QuantumColors.Porcelain, fontWeight = FontWeight.SemiBold)
                Text("Make Quantum yours", style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
            }
            QuantumStar(Modifier.padding(end = 8.dp).then(Modifier), glow = false)
        }

        SettingsCard {
            Section("ASSISTANT")
            OutlinedTextField(
                value = settings.assistantName, onValueChange = repo::setAssistantName,
                label = { Text("Assistant name") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Text("Speech speed · " + String.format(Locale.US, "%.1fx", settings.speechSpeed), style = MaterialTheme.typography.bodyLarge, color = QuantumColors.Porcelain, modifier = Modifier.padding(top = 12.dp))
            Slider(value = settings.speechSpeed, onValueChange = { repo.setSpeechSpeed(Math.round(it * 10) / 10f) }, valueRange = 0.5f..2.0f, steps = 14)
            Note("Saved now. Speech speed will apply when text-to-speech is implemented.")
        }

        SettingsCard {
            Section("MEMORY")
            MemoryMode.entries.forEach { mode ->
                Row(
                    Modifier.fillMaxWidth().clickable(role = Role.RadioButton) { repo.setMemoryMode(mode) }.padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = settings.memoryMode == mode, onClick = null)
                    Column(Modifier.padding(start = 10.dp)) {
                        Text(mode.label, color = QuantumColors.Porcelain, fontWeight = FontWeight.Medium)
                        Text(mode.description, style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
                    }
                }
            }
            Note("Your choice is saved. Memory features are not active yet.")
        }

        SettingsCard {
            Section("ON-DEVICE INTELLIGENCE")
            Text("Device information", style = MaterialTheme.typography.titleMedium, color = QuantumColors.Porcelain)
            Text(
                "RAM: ${device.totalRamMb} MB total · ${device.availableRamMb} MB available\nAndroid API ${device.sdkInt} · ${device.abis.joinToString()}",
                style = MaterialTheme.typography.bodyMedium, color = QuantumColors.TextMuted,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text("Preferred model tier", style = MaterialTheme.typography.titleMedium, color = QuantumColors.Porcelain, modifier = Modifier.padding(top = 16.dp))
            TierRow("Automatic", "Recommended: ${assessment.recommended?.label ?: "none"}", settings.selectedTier == null) { repo.setSelectedTier(null) }
            assessment.tiers.forEach { t ->
                val detail = t.verdict.label + if (t.reasons.isEmpty()) "" else ": " + t.reasons.joinToString("; ")
                TierRow(t.tier.label, detail, settings.selectedTier == t.tier) { repo.setSelectedTier(t.tier) }
            }
            Note("Measurements: ${assessment.measured.joinToString()}. Not measured: ${assessment.notMeasured.joinToString()}. Tier requirements are provisional; no models are installed.")
        }

        SettingsCard {
            Section("PERMISSIONS")
            Note("Quantum currently requests no permissions and does not listen in the background. Permissions will be requested only when a feature needs them.")
            Button(
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)))
                },
                modifier = Modifier.padding(top = 10.dp)
            ) { Text("Open Android app settings") }
        }

        SettingsCard {
            Section("ABOUT")
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuantumStar(Modifier.padding(end = 12.dp), glow = false)
                Column {
                    Text("Quantum", style = MaterialTheme.typography.titleLarge, color = QuantumColors.Porcelain, fontWeight = FontWeight.SemiBold)
                    Text("Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
                }
            }
            Note("Cloud AI, local models, voices and device actions are not available in this build.")
        }
        Text("Made to feel personal. Designed to stay out of your way.", style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted, modifier = Modifier.padding(top = 6.dp, bottom = 30.dp))
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

@Composable
private fun Section(title: String) {
    Text(title, style = MaterialTheme.typography.labelMedium, color = QuantumColors.AccentBright, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
}

@Composable
private fun Note(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
}

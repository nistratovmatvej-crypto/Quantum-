package com.quantum.assistant.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import com.quantum.assistant.ui.theme.QuantumColors
import java.util.Locale

@Composable
fun SettingsScreen(repo: SettingsRepository, settings: AssistantSettings, onBack: () -> Unit) {
    val context = LocalContext.current
    val device = remember { DeviceSnapshotReader.read(context) }
    val assessment = remember(device) { ThresholdCapabilityAssessor().assess(device) }

    Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            Text("Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }

        Section("Assistant")
        OutlinedTextField(
            value = settings.assistantName, onValueChange = repo::setAssistantName,
            label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Text("Speech speed: " + String.format(Locale.US, "%.1fx", settings.speechSpeed), style = MaterialTheme.typography.bodyLarge)
        Slider(value = settings.speechSpeed, onValueChange = { repo.setSpeechSpeed(Math.round(it * 10) / 10f) }, valueRange = 0.5f..2.0f, steps = 14)
        Note("Saved now; applied once text-to-speech ships (Alpha 0.2). Defaults: friendly, concise, honest; follows device language.")

        Section("Memory")
        MemoryMode.entries.forEach { mode ->
            Row(
                Modifier.fillMaxWidth().clickable(role = Role.RadioButton) { repo.setMemoryMode(mode) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = settings.memoryMode == mode, onClick = null, modifier = Modifier.padding(end = 12.dp))
                Column { Text(mode.label); Text(mode.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }            }
        }
        Note("Choice is saved. Nothing is remembered yet because no conversations exist; memory arrives in Alpha 0.5. Default is Off.")

        Section("This device")
        Text("RAM: ${device.totalRamMb} MB total, ${device.availableRamMb} MB free\nAndroid API ${device.sdkInt} - ${device.abis.joinToString()}")
        Text("Local model tier (no models are installed; choice is saved only)", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
        TierRow("Automatic", "Recommended: ${assessment.recommended?.label ?: "none"}", settings.selectedTier == null) { repo.setSelectedTier(null) }
        assessment.tiers.forEach { t ->
            val detail = t.verdict.label + if (t.reasons.isEmpty()) "" else ": " + t.reasons.joinToString("; ")
            TierRow(t.tier.label, detail, settings.selectedTier == t.tier) { repo.setSelectedTier(t.tier) }
        }
        Note("Based on: " + assessment.measured.joinToString() + ". Not measured: " + assessment.notMeasured.joinToString() +
            ". Requirement numbers are placeholders until real models are evaluated; a tier is never a promise it will run.")

        Section("Permissions")
        Note("Alpha 0.1 requests no permissions: the microphone is never opened and no overlay is drawn. Permissions will be requested only when a feature needs them.")
        Button(onClick = {
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)))
        }) { Text("Open Android app settings") }

        Section("About")
        Text("Quantum ${BuildConfig.VERSION_NAME}")
        Text("Not available yet: cloud/local AI, voices, device actions, Supernova.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 32.dp))
    }
}

@Composable
private fun TierRow(title: String, detail: String, selected: Boolean, onSelect: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(role = Role.RadioButton, onClick = onSelect), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = null, modifier = Modifier.padding(end = 12.dp))
        Column { Text(title); Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun Section(title: String) =
    Text(title, style = MaterialTheme.typography.titleMedium, color = QuantumColors.Cyan, modifier = Modifier.padding(top = 16.dp))

@Composable
private fun Note(text: String) = Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

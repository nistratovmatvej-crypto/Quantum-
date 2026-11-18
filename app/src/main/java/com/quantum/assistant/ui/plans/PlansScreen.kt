package com.quantum.assistant.ui.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quantum.assistant.device.LocalTier
import com.quantum.assistant.ui.theme.QuantumColors

@Composable
fun PlansScreen(selectedTier: LocalTier?, onSelectTier: (LocalTier?) -> Unit, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(QuantumColors.Background).safeDrawingPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = QuantumColors.Porcelain)
            }
            Column {
                Text("Уровни Quantum", style = MaterialTheme.typography.headlineSmall, color = QuantumColors.Porcelain, fontWeight = FontWeight.SemiBold)
                Text("Выбери подходящий локальный движок", color = QuantumColors.TextMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
        Text("ЛОКАЛЬНЫЕ УРОВНИ", color = QuantumColors.AccentBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        TierCard("Quantum Meteor", "Компактный движок для устройств с ограниченными ресурсами.", "Быстрее и экономнее, но с более скромными возможностями.", selectedTier == LocalTier.METEOR) { onSelectTier(LocalTier.METEOR) }
        TierCard("Quantum Comet", "Баланс скорости и качества для большинства устройств.", "Рекомендуемый средний уровень. Фактическая совместимость будет проверяться отдельно.", selectedTier == LocalTier.COMET) { onSelectTier(LocalTier.COMET) }
        TierCard("Quantum Nova", "Самый требовательный локальный уровень.", "Нужны достаточная память и подходящая модель. Не обещаем запуск до тестирования.", selectedTier == LocalTier.NOVA) { onSelectTier(LocalTier.NOVA) }
        Button(onClick = { onSelectTier(null) }, modifier = Modifier.fillMaxWidth()) { Text("Автоматический выбор") }
        Text("Выбор уровня сохраняется на устройстве. Сами модели пока не встроены и не скачиваются.", color = QuantumColors.TextMuted, style = MaterialTheme.typography.bodySmall)

        Text("ПОДПИСКА", color = QuantumColors.AccentBright, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Card(
            Modifier.fillMaxWidth().border(1.dp, QuantumColors.Accent.copy(alpha = 0.8f), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = QuantumColors.BackgroundElevated)
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("Quantum Supernova Pro", color = QuantumColors.Porcelain, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text("Расширенные облачные лимиты и дополнительные возможности после их запуска.", color = QuantumColors.TextMuted)
                Text("Месячный и годовой планы будут доступны через Google Play.", color = QuantumColors.Porcelain)
                Text("Покупки пока отключены: продукты и платёжный профиль Google Play ещё не настроены.", color = QuantumColors.Warning, style = MaterialTheme.typography.bodySmall)
                Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) { Text("Подписка скоро появится") }
            }
        }
        Text("Meteor / Comet / Nova — уровни локальной модели, а не платные подписки. Supernova Pro — отдельная будущая подписка.", color = QuantumColors.TextMuted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 24.dp))
    }
}

@Composable
private fun TierCard(title: String, summary: String, detail: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().border(1.dp, if (selected) QuantumColors.AccentBright else QuantumColors.Outline, RoundedCornerShape(22.dp))
            .clickable(role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = QuantumColors.Surface)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = null)
            Column(Modifier.padding(start = 10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = QuantumColors.Porcelain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(summary, color = QuantumColors.Porcelain, style = MaterialTheme.typography.bodyMedium)
                Text(detail, color = QuantumColors.TextMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

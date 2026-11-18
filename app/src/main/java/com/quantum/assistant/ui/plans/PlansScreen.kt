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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quantum.assistant.device.LocalTier
import com.quantum.assistant.ui.QuantumStar
import com.quantum.assistant.ui.theme.QuantumColors

@Composable
fun PlansScreen(selectedTier: LocalTier?, onSelectTier: (LocalTier?) -> Unit, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(QuantumColors.Background).safeDrawingPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = QuantumColors.Porcelain) }
            Column(Modifier.weight(1f)) {
                Text("Возможности", style = MaterialTheme.typography.headlineMedium, color = QuantumColors.Porcelain)
                Text("Локальные модели и планы Quantum", style = MaterialTheme.typography.bodySmall, color = QuantumColors.TextMuted)
            }
            QuantumStar(Modifier.padding(end = 4.dp))
        }

        Text("ЛОКАЛЬНЫЕ МОДЕЛИ", style = MaterialTheme.typography.labelMedium, color = QuantumColors.AccentBright)
        Text("Выбери профиль устройства", style = MaterialTheme.typography.titleLarge, color = QuantumColors.Porcelain)
        TierCard("Quantum Meteor", "Лёгкий профиль", "Для устройств с ограниченными ресурсами. Меньше требований, но модели пока нет.", selectedTier == LocalTier.METEOR) { onSelectTier(LocalTier.METEOR) }
        TierCard("Quantum Comet", "Сбалансированный профиль", "Компромисс ресурсов и качества. Реальная совместимость требует тестирования.", selectedTier == LocalTier.COMET) { onSelectTier(LocalTier.COMET) }
        TierCard("Quantum Nova", "Продвинутый профиль", "Самый требовательный вариант. Модель не поставляется и пока недоступна для загрузки.", selectedTier == LocalTier.NOVA) { onSelectTier(LocalTier.NOVA) }
        Button(onClick = { onSelectTier(null) }, modifier = Modifier.fillMaxWidth()) { Text("Автоматический выбор") }
        Text("Все три варианта сейчас сохраняют только предпочтение. Файлы моделей и размеры загрузки ещё не опубликованы.", color = QuantumColors.TextMuted, style = MaterialTheme.typography.bodySmall)

        Text("ПОДПИСКА", style = MaterialTheme.typography.labelMedium, color = QuantumColors.AccentBright, modifier = Modifier.padding(top = 18.dp))
        Card(
            Modifier.fillMaxWidth().border(1.dp, QuantumColors.Accent.copy(alpha = .55f), RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = QuantumColors.BackgroundElevated)
        ) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("SUPERNOVA", style = MaterialTheme.typography.labelMedium, color = QuantumColors.AccentBright)
                Text("Quantum Pro", style = MaterialTheme.typography.headlineMedium, color = QuantumColors.Porcelain)
                Text("Будущий расширенный план для облачных возможностей и дополнительных сервисов.", style = MaterialTheme.typography.bodyMedium, color = QuantumColors.TextMuted)
                FeatureLine("Бесплатная версия", "Текущая базовая функциональность")
                FeatureLine("Месячный план", "Цена ещё не задана")
                FeatureLine("Годовой план", "Цена ещё не задана")
                Text("Google Play Billing не подключён. Покупки, восстановление и статус подписки пока не работают.", style = MaterialTheme.typography.bodySmall, color = QuantumColors.Warning)
                Text("Покупки появятся после подключения платёжной инфраструктуры.", style = MaterialTheme.typography.labelLarge, color = QuantumColors.TextMuted)
            }
        }
        Text("Meteor / Comet / Nova — профили локальной модели, а не подписки. Supernova Pro — отдельный будущий тариф.", color = QuantumColors.TextMuted, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 28.dp))
    }
}

@Composable
private fun TierCard(title: String, subtitle: String, detail: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().border(1.dp, if (selected) QuantumColors.AccentBright else QuantumColors.Outline.copy(alpha = .55f), RoundedCornerShape(24.dp))
            .clickable(role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) QuantumColors.SurfaceRaised else QuantumColors.Surface)
    ) {
        Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = null)
            Column(Modifier.padding(start = 10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(title, color = QuantumColors.Porcelain, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = QuantumColors.AccentBright, style = MaterialTheme.typography.labelMedium)
                Text(detail, color = QuantumColors.TextMuted, style = MaterialTheme.typography.bodySmall)
                Text("НЕ ДОСТУПНО ДЛЯ ЗАГРУЗКИ", color = QuantumColors.Warning, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun FeatureLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = QuantumColors.Porcelain, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = QuantumColors.TextMuted, style = MaterialTheme.typography.bodySmall)
    }
}

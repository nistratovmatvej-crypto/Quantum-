package com.quantum.assistant.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quantum.assistant.ui.QuantumStar
import com.quantum.assistant.ui.theme.QuantumColors

@Composable
fun WelcomeScreen(onContinue: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    val valid = name.trim().isNotEmpty()
    Box(Modifier.fillMaxSize().background(QuantumColors.Background).padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxWidth().background(QuantumColors.BackgroundElevated, RoundedCornerShape(34.dp)).padding(horizontal = 25.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            QuantumStar(Modifier.size(104.dp), glow = true)
            Spacer(Modifier.height(27.dp))
            Text("QUANTUM", color = QuantumColors.AccentBright, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text("Технологии ближе.", color = QuantumColors.Porcelain, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
            Text(
                "Персональный помощник, который включается только тогда, когда ты этого хочешь.",
                color = QuantumColors.TextMuted, style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 10.dp)
            )
            AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { it / 5 })) {
                Column {
                    Text("Как к тебе обращаться?", color = QuantumColors.Porcelain, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 30.dp, bottom = 8.dp))
                    OutlinedTextField(
                        value = name, onValueChange = { name = it.take(32) },
                        modifier = Modifier.fillMaxWidth(), label = { Text("Имя или псевдоним") }, singleLine = true
                    )
                    Button(onClick = { if (valid) onContinue(name.trim()) }, enabled = valid, modifier = Modifier.fillMaxWidth().padding(top = 18.dp)) {
                        Text("Продолжить")
                    }
                }
            }
            Text(
                "Это локальный профиль на устройстве. Регистрация и пароль не требуются. Доступ к микрофону будет запрошен отдельно — только при его включении.",
                color = QuantumColors.TextMuted, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 18.dp)
            )
        }
    }
}

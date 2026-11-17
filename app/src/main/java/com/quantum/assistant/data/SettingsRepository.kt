package com.quantum.assistant.data

import android.content.SharedPreferences
import com.quantum.assistant.device.LocalTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Memory modes from PRODUCT_SPEC. Alpha 0.1 only stores the choice. */
enum class MemoryMode(val label: String, val description: String) {
    OFF("Off", "No cross-session memory."),
    DIALOGUE_ONLY("Dialogue only", "Retain conversation history."),
    FULL("Full", "Retain useful facts and preferences."),
    FULL_CONFIGURABLE("Full + configurable", "Full memory with review, edit, pin and delete.")
}

data class AssistantSettings(
    val assistantName: String = DEFAULT_NAME,
    val speechSpeed: Float = 1.0f,
    val memoryMode: MemoryMode = MemoryMode.OFF,
    /** User's explicit local-tier choice; null = follow the (advisory) recommendation. No model is installed yet. */
    val selectedTier: LocalTier? = null
) {
    val displayName: String get() = assistantName.trim().ifEmpty { DEFAULT_NAME }

    companion object { const val DEFAULT_NAME = "Quantum" }
}

/** Tiny storage seam so persistence is unit-testable without Android. */
interface KeyValueStore {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
}

class SharedPrefsStore(private val prefs: SharedPreferences) : KeyValueStore {
    override fun getString(key: String): String? = prefs.getString(key, null)
    override fun putString(key: String, value: String) { prefs.edit().putString(key, value).apply() }
}

/** Non-sensitive settings only. Reads once at construction; every change is written immediately. */
class SettingsRepository(private val store: KeyValueStore) {
    private val _settings = MutableStateFlow(load())
    val settings: StateFlow<AssistantSettings> = _settings.asStateFlow()

    private fun load(): AssistantSettings {
        val d = AssistantSettings()
        return AssistantSettings(
            assistantName = (store.getString(K_NAME) ?: d.assistantName).take(MAX_NAME),
            speechSpeed = (store.getString(K_SPEED)?.toFloatOrNull() ?: d.speechSpeed).coerceIn(MIN_SPEED, MAX_SPEED),
            memoryMode = MemoryMode.entries.firstOrNull { it.name == store.getString(K_MEMORY) } ?: d.memoryMode,
            selectedTier = LocalTier.entries.firstOrNull { it.name == store.getString(K_TIER) }
        )
    }

    fun setAssistantName(name: String) {
        val v = name.take(MAX_NAME)
        _settings.value = _settings.value.copy(assistantName = v); store.putString(K_NAME, v)
    }

    fun setSpeechSpeed(speed: Float) {
        val v = speed.coerceIn(MIN_SPEED, MAX_SPEED)
        _settings.value = _settings.value.copy(speechSpeed = v); store.putString(K_SPEED, v.toString())
    }

    fun setMemoryMode(mode: MemoryMode) {
        _settings.value = _settings.value.copy(memoryMode = mode); store.putString(K_MEMORY, mode.name)
    }

    fun setSelectedTier(tier: LocalTier?) {
        _settings.value = _settings.value.copy(selectedTier = tier); store.putString(K_TIER, tier?.name ?: "")
    }

    companion object {
        const val MAX_NAME = 30
        const val MIN_SPEED = 0.5f
        const val MAX_SPEED = 2.0f
        private const val K_NAME = "assistant_name"
        private const val K_SPEED = "speech_speed"
        private const val K_MEMORY = "memory_mode"
        private const val K_TIER = "selected_tier"
    }
}

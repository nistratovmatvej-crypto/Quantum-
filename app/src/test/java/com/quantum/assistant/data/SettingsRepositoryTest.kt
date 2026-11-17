package com.quantum.assistant.data

import com.quantum.assistant.device.LocalTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MapStore(val map: MutableMap<String, String> = mutableMapOf()) : KeyValueStore {
    override fun getString(key: String) = map[key]
    override fun putString(key: String, value: String) { map[key] = value }
}

class SettingsRepositoryTest {
    @Test fun defaultsWhenEmpty() {
        val s = SettingsRepository(MapStore()).settings.value
        assertEquals("Quantum", s.displayName); assertEquals(1.0f, s.speechSpeed); assertEquals(MemoryMode.OFF, s.memoryMode); assertNull(s.selectedTier)
    }

    @Test fun valuesSurviveRecreation() {
        val store = MapStore()
        SettingsRepository(store).apply {
            setAssistantName("Orion"); setSpeechSpeed(1.5f); setMemoryMode(MemoryMode.FULL); setSelectedTier(LocalTier.COMET)
        }
        val s = SettingsRepository(store).settings.value // simulates app restart
        assertEquals("Orion", s.assistantName); assertEquals(1.5f, s.speechSpeed)
        assertEquals(MemoryMode.FULL, s.memoryMode); assertEquals(LocalTier.COMET, s.selectedTier)
    }

    @Test fun selectedTierCanBeClearedAndPersists() {
        val store = MapStore()
        SettingsRepository(store).apply { setSelectedTier(LocalTier.NOVA); setSelectedTier(null) }
        assertNull(SettingsRepository(store).settings.value.selectedTier)
    }

    @Test fun flowReflectsChanges() {
        val repo = SettingsRepository(MapStore())
        repo.setAssistantName("Nova")
        assertEquals("Nova", repo.settings.value.displayName)
    }

    @Test fun blankNameFallsBackToDefaultForDisplay() {
        val repo = SettingsRepository(MapStore()); repo.setAssistantName("   ")
        assertEquals("Quantum", repo.settings.value.displayName)
    }

    @Test fun speedAndNameAreClamped() {
        val repo = SettingsRepository(MapStore())
        repo.setSpeechSpeed(9f); assertEquals(2.0f, repo.settings.value.speechSpeed)
        repo.setAssistantName("x".repeat(100)); assertEquals(30, repo.settings.value.assistantName.length)
    }

    @Test fun corruptStoredValuesFallBackToDefaults() {
        val s = SettingsRepository(MapStore(mutableMapOf("speech_speed" to "abc", "memory_mode" to "???", "selected_tier" to "X"))).settings.value
        assertEquals(1.0f, s.speechSpeed); assertEquals(MemoryMode.OFF, s.memoryMode); assertNull(s.selectedTier)
    }
}

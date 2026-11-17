package com.quantum.assistant

import android.app.Application
import android.content.Context
import com.quantum.assistant.data.SettingsRepository
import com.quantum.assistant.data.SharedPrefsStore

/** Minimal manual dependency wiring. Swap for DI later if the graph grows. */
class QuantumApp : Application() {
    lateinit var settings: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        settings = SettingsRepository(SharedPrefsStore(getSharedPreferences("quantum_settings", Context.MODE_PRIVATE)))
    }
}

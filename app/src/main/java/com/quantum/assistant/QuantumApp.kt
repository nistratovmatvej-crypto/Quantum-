package com.quantum.assistant

import android.app.Application
import android.content.Context
import com.quantum.assistant.audio.AndroidSpeechRecognizerSession
import com.quantum.assistant.data.ProfileStore
import com.quantum.assistant.data.SettingsRepository
import com.quantum.assistant.data.SharedPrefsStore

class QuantumApp : Application() {
    lateinit var settings: SettingsRepository
        private set
    lateinit var profile: ProfileStore
        private set
    val micSession by lazy { AndroidSpeechRecognizerSession(this) }

    override fun onCreate() {
        super.onCreate()
        settings = SettingsRepository(SharedPrefsStore(getSharedPreferences("quantum_settings", Context.MODE_PRIVATE)))
        profile = ProfileStore(getSharedPreferences("quantum_profile", Context.MODE_PRIVATE))
    }
}

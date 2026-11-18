package com.quantum.assistant.data

import android.content.SharedPreferences

/**
 * Local-only profile for the first launch. This is deliberately NOT presented
 * as a cloud account or secure authentication; sync/sign-in provider comes later.
 */
class ProfileStore(private val prefs: SharedPreferences) {
    fun hasProfile(): Boolean = !displayName().isNullOrBlank()
    fun displayName(): String? = prefs.getString(KEY_NAME, null)?.trim()?.takeIf { it.isNotEmpty() }
    fun saveProfile(name: String) {
        val clean = name.trim().take(MAX_NAME)
        require(clean.isNotEmpty()) { "Profile name must not be empty." }
        prefs.edit().putString(KEY_NAME, clean).apply()
    }
    fun clearProfile() { prefs.edit().remove(KEY_NAME).apply() }

    companion object {
        const val MAX_NAME = 32
        private const val KEY_NAME = "local_profile_name"
    }
}

package com.quantum.assistant

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quantum.assistant.floating.QuantumIslandService
import com.quantum.assistant.ui.home.HomeScreen
import com.quantum.assistant.ui.home.HomeViewModel
import com.quantum.assistant.ui.home.HomeViewModelFactory
import com.quantum.assistant.ui.login.WelcomeScreen
import com.quantum.assistant.ui.plans.PlansScreen
import com.quantum.assistant.ui.settings.SettingsScreen
import com.quantum.assistant.ui.theme.QuantumColors
import com.quantum.assistant.ui.theme.QuantumTheme

class MainActivity : ComponentActivity() {
    private val overlayPrefs by lazy { getSharedPreferences("quantum_overlay", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as QuantumApp
        setContent {
            QuantumTheme {
                val nav = rememberNavController()
                val settings by app.settings.settings.collectAsState()
                val start = if (app.profile.hasProfile()) "home" else "welcome"
                val homeVm: HomeViewModel = viewModel(factory = HomeViewModelFactory(app.micSession))
                Box(Modifier.fillMaxSize().background(QuantumColors.Background)) {
                    NavHost(nav, startDestination = start) {
                        composable("welcome") {
                            WelcomeScreen { displayName ->
                                app.profile.saveProfile(displayName)
                                nav.navigate("home") { popUpTo("welcome") { inclusive = true } }
                            }
                        }
                        composable("home") {
                            HomeScreen(
                                vm = homeVm,
                                settings = settings,
                                profileName = app.profile.displayName(),
                                onOpenSettings = { nav.navigate("settings") },
                                onOpenPlans = { nav.navigate("plans") },
                                onEnableIsland = { enableIsland() }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(app.settings, settings) { nav.popBackStack() }
                        }
                        composable("plans") {
                            PlansScreen(
                                selectedTier = settings.selectedTier,
                                onSelectTier = app.settings::setSelectedTier,
                                onBack = { nav.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun enableIsland() {
        overlayPrefs.edit().putBoolean("enabled", true).apply()
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            return
        }
        startIslandService(QuantumIslandService.ACTION_START)
    }

    private fun startIslandService(action: String) {
        val intent = Intent(this, QuantumIslandService::class.java).setAction(action)
        androidx.core.content.ContextCompat.startForegroundService(this, intent)
    }

    override fun onResume() {
        super.onResume()
        if (overlayPrefs.getBoolean("enabled", false) && Settings.canDrawOverlays(this)) {
            startIslandService(QuantumIslandService.ACTION_RESTORE)
        }
    }

    override fun onStop() {
        val app = application as QuantumApp
        ViewModelProvider(this, HomeViewModelFactory(app.micSession))[HomeViewModel::class.java].panel.onClose()
        if (overlayPrefs.getBoolean("enabled", false)) {
            startIslandService(QuantumIslandService.ACTION_COMPACT)
        }
        super.onStop()
    }
}
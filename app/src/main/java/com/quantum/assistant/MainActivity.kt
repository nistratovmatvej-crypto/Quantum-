package com.quantum.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quantum.assistant.ui.home.HomeScreen
import com.quantum.assistant.ui.home.HomeViewModel
import com.quantum.assistant.ui.settings.SettingsScreen
import com.quantum.assistant.ui.theme.QuantumColors
import com.quantum.assistant.ui.theme.QuantumTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repo = (application as QuantumApp).settings
        setContent {
            QuantumTheme {
                val nav = rememberNavController()
                val settings by repo.settings.collectAsState()
                val homeVm: HomeViewModel = viewModel()
                Box(Modifier.fillMaxSize().background(QuantumColors.Navy)) {
                    NavHost(nav, startDestination = "home") {
                        composable("home") { HomeScreen(homeVm, settings) { nav.navigate("settings") } }
                        composable("settings") { SettingsScreen(repo, settings) { nav.popBackStack() } }
                    }
                }
            }
        }
    }

    /** Never keep a mic session alive in the background. */
    override fun onStop() {
        ViewModelProvider(this)[HomeViewModel::class.java].panel.onClose()
        super.onStop()
    }
}

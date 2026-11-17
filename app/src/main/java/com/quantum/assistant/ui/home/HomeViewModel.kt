package com.quantum.assistant.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quantum.assistant.audio.NoOpMicSession
import com.quantum.assistant.floating.PanelController

class HomeViewModel : ViewModel() {
    val panel = PanelController(viewModelScope, NoOpMicSession)

    override fun onCleared() { panel.onClose() }
}

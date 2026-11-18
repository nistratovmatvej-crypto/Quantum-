package com.quantum.assistant.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quantum.assistant.audio.MicSession
import com.quantum.assistant.floating.PanelController

class HomeViewModel(mic: MicSession) : ViewModel() {
    val panel = PanelController(viewModelScope, mic)
    override fun onCleared() { panel.onClose() }
}

class HomeViewModelFactory(private val mic: MicSession) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
        return HomeViewModel(mic) as T
    }
}

package org.lightwork.guapui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {
    private val _uri = MutableStateFlow("")
    val uri: StateFlow<String> = _uri.asStateFlow()

    fun setUri(uri: String) {
        _uri.value = uri
        println(uri)
    }
}
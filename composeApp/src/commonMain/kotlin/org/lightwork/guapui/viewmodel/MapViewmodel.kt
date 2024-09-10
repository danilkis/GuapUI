package org.lightwork.guapui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {
    private val _uri = MutableStateFlow("")
    private val _unstable = MutableStateFlow(true)
    val uri: StateFlow<String> = _uri.asStateFlow()

    fun setUri(uri: String) {
        _uri.value = uri
        println(uri)
    }

    val unstableWarning: StateFlow<Boolean> = _unstable.asStateFlow()

    fun setUnstable(bool: Boolean) {
        _unstable.value = bool
        println(uri)
    }
}
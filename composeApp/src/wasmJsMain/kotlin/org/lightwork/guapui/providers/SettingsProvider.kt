package org.lightwork.guapui.providers

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings

actual class SettingsProvider {
    actual fun getSettings(): Settings {
        return StorageSettings()  // Uses browser's localStorage
    }
}

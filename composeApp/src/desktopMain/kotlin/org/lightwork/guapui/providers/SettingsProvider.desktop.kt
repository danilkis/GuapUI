package org.lightwork.guapui.providers

import com.russhwolf.settings.PropertiesSettings
import com.russhwolf.settings.Settings
import java.util.Properties
import java.io.FileInputStream
actual class SettingsProvider {
    actual fun getSettings(): Settings {
        // Load properties from the settings file
        val properties = Properties()
        val file = java.io.File("settings.properties")

        // If the file exists, load the properties
        if (file.exists()) {
            FileInputStream(file).use { properties.load(it) }
        }

        // Return a PropertiesSettings instance using the loaded properties
        return PropertiesSettings(properties)
    }
}

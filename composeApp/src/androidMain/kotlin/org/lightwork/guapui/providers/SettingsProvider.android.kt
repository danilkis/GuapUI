package org.lightwork.guapui.providers

import android.content.Context
import android.content.SharedPreferences
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

actual class SettingsProvider(private val context: Context) {
    actual fun getSettings(): Settings {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return SharedPreferencesSettings(sharedPreferences)
    }
}
package org.lightwork.guapui.providers

import com.russhwolf.settings.Settings

expect class SettingsProvider {
    fun getSettings(): Settings
}
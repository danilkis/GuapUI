package org.lightwork.guapui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import org.lightwork.guapui.providers.SettingsProvider
import org.lightwork.guapui.view.ScheduleApp
import org.w3c.dom.Window

@Composable
actual fun App(url: String) {
    val isDarkTheme = isSystemInDarkTheme()
    AppTheme(isDarkTheme, false, { ScheduleApp(settingsProvider = SettingsProvider(), url) })
}
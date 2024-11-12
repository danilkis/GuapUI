package org.lightwork.guapui

import AndroidAppTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.lightwork.guapui.helper.SupabaseHelper
import org.lightwork.guapui.providers.SettingsProvider
import org.lightwork.guapui.view.ScheduleApp

@Composable
actual fun App(url: String) {
    val isDarkTheme = isSystemInDarkTheme()
    val supabaseHelper = SupabaseHelper(
        supabaseUrl = "https://vjfdmvrkriajftklozgf.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZqZmRtdnJrcmlhamZ0a2xvemdmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzA4MzYyMTUsImV4cCI6MjA0NjQxMjIxNX0.RgDjBvwNacNVLK2vqCt-2i-0kx6MaSKEUUWokfc_fcc"
    )
    val ctx = LocalContext.current
    AndroidAppTheme(isDarkTheme, true, { ScheduleApp(supabaseHelper, settingsProvider = SettingsProvider(ctx), url) })
}
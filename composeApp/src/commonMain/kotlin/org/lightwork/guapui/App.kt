package org.lightwork.guapui

import Overview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*

@Composable
fun App() {
    val isDarkTheme = isSystemInDarkTheme()
    AppTheme(isDarkTheme, false, { Overview() })
}
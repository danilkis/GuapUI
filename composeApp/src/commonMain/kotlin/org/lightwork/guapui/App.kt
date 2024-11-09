package org.lightwork.guapui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.lightwork.guapui.view.MapPage
import org.lightwork.guapui.view.Overview
import org.lightwork.guapui.view.ScheduleApp

@Composable expect
fun App()
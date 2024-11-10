package org.lightwork.guapui.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import org.lightwork.guapui.viewmodel.MapViewModel

@Composable
expect fun MapPage(
    navController: NavController,
    mapViewModel: MapViewModel
)
package org.lightwork.guapui.functions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavController
import org.lightwork.guapui.getPlatform

@Composable
fun NavigateBasedOnPlatform(navController: NavController, roomUri: String) {
    val platform = getPlatform()
    val uriHandler = LocalUriHandler.current
    when {
        "Java" in platform.name || "Android" in platform.name -> {
            navController.navigate("map_page/$roomUri") // Pass the URI to the destination
        }
        "Web" in platform.name -> {
            uriHandler.openUri(roomUri)
        }
        else -> {
            // Handle other platforms if needed
        }
    }
}
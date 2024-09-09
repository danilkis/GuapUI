package org.lightwork.guapui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.datlag.kcef.KCEF
import dev.datlag.kcef.KCEF.init
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lightwork.guapui.view.SplashScreen
import org.lightwork.guapui.view.WebLoadingScreen
import java.io.File
import kotlin.math.max

fun main(args: Array<String>) = application {
    Window(onCloseRequest = ::exitApplication) {
        var restartRequired by remember { mutableStateOf(false) }
        var downloading by remember { mutableStateOf(0F) }
        var initialized by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                init(builder = {
                    installDir(File("kcef-bundle"))
                    progress {
                        onDownloading {
                            downloading = max(it, 0F)
                            println(downloading)
                        }
                        onInitialized {
                            initialized = true
                        }
                    }
                    settings {
                        cachePath = File("cache").absolutePath
                    }
                }, onError = {
                    if (it != null) {
                        it.printStackTrace()
                    }
                }, onRestartRequired = {
                    restartRequired = true
                })
            }
        }

        if (restartRequired) {
            println("Restart required.")
            Text(text = "Restart required.")
        } else {
            if (initialized) {
                // Display the main app when initialized
                App()
            } else {
                val isDarkTheme = isSystemInDarkTheme()
                AppTheme(isDarkTheme, false, { WebLoadingScreen(downloading) })
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                KCEF.disposeBlocking()
            }
        }
    }
}
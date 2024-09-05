package org.lightwork.guapui

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.datlag.kcef.KCEF
import dev.datlag.kcef.KCEF.init
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lightwork.guapui.view.SplashScreen
import java.io.File
import kotlin.math.max


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        var restartRequired by remember { mutableStateOf(false) }
        var downloading by remember { mutableStateOf(0F) }
        var initialized by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                KCEF.init(builder = {
                    installDir(File("kcef-bundle"))
                    progress {
                        onDownloading {
                            downloading = max(it, 0F)
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
            Text(text = "Restart required.")
        } else {
            if (initialized) {
                // Display the main app when initialized
                App()
            } else {
                // Show splash screen until initialization is done
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                KCEF.disposeBlocking()
            }
        }
    }
}
package org.lightwork.guapui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
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
    var windowTitle by remember { mutableStateOf("SuaiUI") }
    val isDarkTheme = isSystemInDarkTheme()
    val appSupportDir = File(System.getProperty("user.home"), "Library/Application Support/SuaiUI")
    if (!appSupportDir.exists()) {
        appSupportDir.mkdirs() // Create the directory if it doesn't exist
    }
    Window(onCloseRequest = ::exitApplication, title = windowTitle) {
        var restartRequired by remember { mutableStateOf(false) }
        var downloading by remember { mutableStateOf(0F) }
        var initialized by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                init(builder = {
                    if (System.getProperty("os.name").contains("Mac")) {
                        installDir(File(appSupportDir, "kcef-bundle"))
                    } else {
                        installDir(File("kcef-bundle"))
                    }
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
                        if (System.getProperty("os.name").contains("Mac")) {
                            cachePath = File(appSupportDir, "cache").absolutePath
                        } else {
                            cachePath = File("cache").absolutePath
                        }

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
            AppTheme(isDarkTheme, false, { WebLoadingScreen(0f, "Что-то пошло не так, свяжитесь с разработчиком") })
        } else {
            if (initialized) {
                // Display the main app when initialized
                App()
            } else {
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

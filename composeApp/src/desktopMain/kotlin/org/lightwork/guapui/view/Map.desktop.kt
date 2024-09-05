package org.lightwork.guapui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.multiplatform.webview.util.KLogSeverity
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import org.lightwork.guapui.viewmodel.MapViewModel

@Composable
actual fun MapPage(
    navController: NavController,
    mapViewModel: MapViewModel
) {
    val uri by mapViewModel.uri.collectAsState()
    val webViewState =
            rememberWebViewState(uri)
        Column(Modifier.fillMaxSize()) {
            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize()
            )
        }

}


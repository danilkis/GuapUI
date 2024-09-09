package org.lightwork.guapui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.multiplatform.webview.util.KLogSeverity
import com.multiplatform.webview.web.*
import org.lightwork.guapui.viewmodel.MapViewModel

@Composable
actual fun MapPage(
    navController: NavController,
    mapViewModel: MapViewModel
) {
    val uri by mapViewModel.uri.collectAsState()
    val state = rememberWebViewState(uri)
    val navigator = rememberWebViewNavigator()
    val loadingState = state.loadingState


    Column(
        Modifier
            .fillMaxSize() // Apply the opacity modifier here
    ) {
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                progress = { loadingState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        WebView(
            state = state,
            navigator = navigator,
            modifier = Modifier.fillMaxSize()
        )
    }
}

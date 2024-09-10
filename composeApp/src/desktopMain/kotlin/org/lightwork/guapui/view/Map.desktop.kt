package org.lightwork.guapui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
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
    var showDialog by remember { mutableStateOf(mapViewModel.unstableWarning.value) } // Show dialog if unstableWarning is true
    val uri by mapViewModel.uri.collectAsState()
    val unstableView by mapViewModel.unstableWarning.collectAsState()
    val state = rememberWebViewState(uri)
    val navigator = rememberWebViewNavigator()
    val loadingState = state.loadingState
    state.webSettings.apply {
        desktopWebSettings.apply {
            isJavaScriptEnabled = true
            transparent = true
        }
    }

    Column(
        Modifier
            .fillMaxSize()
    ) {
        if (showDialog && unstableView) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Warning Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = {

                    Text(
                        text = "Нестабильная функциональность",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Text(
                        "Навигация по корпусам на desktop платформах в режиме тестирования!\nПри переходе на навигацию приложение может крашнуться либо просто ничего не показать. \n Если такое произошло, создайте issue на странице github, либо свяжитесь с разработчиком.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false; mapViewModel.setUnstable(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("OK")
                    }
                }
            )
        } else {
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
}


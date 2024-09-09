package org.lightwork.guapui.view

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import guapui.composeapp.generated.resources.Res
import guapui.composeapp.generated.resources.SualBar
import guapui.composeapp.generated.resources.github
import guapui.composeapp.generated.resources.telegram
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.lightwork.guapui.elements.HelperButton
import org.lightwork.guapui.elements.SocialButton
import org.lightwork.guapui.viewmodel.MapViewModel
import org.lightwork.guapui.viewmodel.ScheduleViewModel

enum class AppScreen(val title: String) {
    Main(title = "SuaiUI"),
    Map(title = "Навигатор")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            if (currentScreen.title == "SuaiUI") {
                Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp))
                {
                    Image(
                        painter = painterResource(Res.drawable.SualBar),
                        contentDescription = "SuaiUI Logo",
                        modifier = Modifier
                            .padding(4.dp).size(70.dp) // Add padding to give some space around it
                    )
                    SocialButton("https://github.com/danilkis/GuapUI", Res.drawable.github)
                    SocialButton("https://t.me/SuaiMultiplatform", Res.drawable.telegram)
                    HelperButton()
                }
            } else {
                Text(currentScreen.title)
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            }
        }
    )
}


@Composable
fun ScheduleApp(
    viewModel: ScheduleViewModel = viewModel { ScheduleViewModel() },
    navController: NavHostController = rememberNavController(),
    mapViewModel: MapViewModel = viewModel { MapViewModel() }
) {
    // Track whether the splash screen is visible
    var isSplashScreenVisible by remember { mutableStateOf(true) }

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Main.name
    )

    LaunchedEffect(currentScreen) {
        if (currentScreen != AppScreen.Map) {
            mapViewModel.setUri("")
        }
    }

    Scaffold(
        topBar = {
            if (!isSplashScreenVisible) {
                ScheduleAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            }
        }
    ) { innerPadding ->
        Crossfade(targetState = currentScreen, animationSpec = tween(500)) { screen ->
            NavHost(
                navController = navController,
                startDestination = AppScreen.Main.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(route = AppScreen.Main.name) {
                    Overview(
                        viewModel = viewModel,
                        navController = navController,
                        mapViewModel = mapViewModel,
                        onSplashScreenVisibilityChanged = { isVisible ->
                            isSplashScreenVisible = isVisible
                        }
                    )
                }
                composable(route = AppScreen.Map.name) { backStackEntry ->
                    val uri = backStackEntry.arguments?.getString("uri") ?: ""
                    MapPage(navController, mapViewModel)
                }
            }
        }
    }
}


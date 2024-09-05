package org.lightwork.guapui.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import guapui.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
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
        title = { Text(currentScreen.title) },
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
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Main.name
    )

    Scaffold(
        topBar = {
            ScheduleAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        //val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = AppScreen.Main.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = AppScreen.Main.name) {
                Overview(viewModel, navController, mapViewModel)
            }
            composable(route = AppScreen.Map.name) { backStackEntry ->
                val uri = backStackEntry.arguments?.getString("uri") ?: ""
                MapPage(navController, mapViewModel)
            }
        }
    }
}
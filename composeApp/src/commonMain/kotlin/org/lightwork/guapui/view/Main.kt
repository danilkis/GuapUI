package org.lightwork.guapui.view

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import guapui.composeapp.generated.resources.Res
import guapui.composeapp.generated.resources.SualBar
import org.jetbrains.compose.resources.painterResource
import org.lightwork.guapui.elements.ExpandableGroupField
import org.lightwork.guapui.models.Group
import org.lightwork.guapui.providers.SettingsProvider
import org.lightwork.guapui.viewmodel.CalendarViewModel
import org.lightwork.guapui.viewmodel.MapViewModel
import org.lightwork.guapui.viewmodel.ScheduleViewModel

enum class AppScreen(val title: String) {
    Main(title = "SuaiUI"),
    Map(title = "Навигатор"),
    Onboarding(title = "Start")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    groups: List<Group>?,  // Add groups parameter
    selectedGroupId: Int?,  // Pass selectedGroupId to the field
    onGroupSelected: (Int) -> Unit,  // Callback to handle group selection
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Box(Modifier.fillMaxWidth()) {  // Wrap content in a Box for dropdown expansion
                if (currentScreen.title == "SuaiUI") {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.SualBar),
                            contentDescription = "SuaiUI Logo",
                            modifier = Modifier
                                .padding(4.dp)
                                .size(70.dp)
                        )
                        // Display the ExpandableGroupField in the top bar
                        groups?.let {
                            Box(Modifier.weight(1f)) {  // Allow the field to take up available space
                                ExpandableGroupField(
                                    items = it,
                                    label = "Группа",
                                    selectedGroupId = selectedGroupId, // Pass selectedGroupId
                                    onItemSelected = { id ->
                                        onGroupSelected(id)  // Handle group selection
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text(currentScreen.title)
                }
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
    settingsProvider: SettingsProvider,
    viewModel: ScheduleViewModel = viewModel { ScheduleViewModel(settingsProvider) },
    navController: NavHostController = rememberNavController(),
    mapViewModel: MapViewModel = viewModel { MapViewModel() },
    calendarViewModel: CalendarViewModel = viewModel { CalendarViewModel() }
) {
    var isSplashScreenVisible by remember { mutableStateOf(true) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(backStackEntry?.destination?.route ?: AppScreen.Main.name)

    // Collect the group data from the viewModel
    val groups by viewModel.groups.collectAsState()

    LaunchedEffect(currentScreen) {
        if (currentScreen != AppScreen.Map) {
            mapViewModel.setUri("")
        }
    }

    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    // Set a default screen in the LaunchedEffect to navigate after initialization
    LaunchedEffect(isOnboardingCompleted) {
        if (!isOnboardingCompleted) {
            // Only navigate after the NavHost is initialized
            navController.navigate(AppScreen.Onboarding.name) {
                // Clear the back stack and avoid going back to onboarding
                popUpTo(AppScreen.Main.name) { inclusive = true }
            }
        }
    }

    // Scaffold should be wrapped around a NavHost to ensure proper navigation graph initialization
    Scaffold(
        topBar = {
            if (!isSplashScreenVisible) {
                ScheduleAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    groups = groups,  // Pass the groups data
                    selectedGroupId = viewModel.selectedGroupId,  // Pass selectedGroupId
                    onGroupSelected = { id ->
                        viewModel.selectGroup(id)  // Handle group selection
                    },
                    modifier = Modifier.animateContentSize(animationSpec = tween(durationMillis = 300))
                )
            }
        }
    ) { innerPadding ->
        Crossfade(targetState = currentScreen, animationSpec = tween(500)) { screen ->
            // Ensure the NavHost is set up inside the Composable
            NavHost(
                navController = navController,
                startDestination = if (isOnboardingCompleted) AppScreen.Main.name else AppScreen.Onboarding.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(route = AppScreen.Main.name) {
                    Overview(
                        viewModel = viewModel,
                        navController = navController,
                        mapViewModel = mapViewModel,
                        calendarViewModel = calendarViewModel,
                        onSplashScreenVisibilityChanged = { isVisible ->
                            isSplashScreenVisible = isVisible
                        }
                    )
                }
                composable(route = AppScreen.Onboarding.name) {
                    OnboardingScreen(
                        onComplete = {
                            viewModel.completeOnboarding()  // Mark onboarding as complete
                            navController.navigate(AppScreen.Main.name) {
                                popUpTo(AppScreen.Onboarding.name) { inclusive = true }
                            }
                        }
                    )
                }
                composable(route = AppScreen.Map.name) { backStackEntry ->
                    MapPage(navController, mapViewModel)
                }
            }
        }
    }
}





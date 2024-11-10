package org.lightwork.guapui.view

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
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
import org.lightwork.guapui.viewmodel.*

enum class AppScreen(val title: String) {
    Main(title = "SuaiUI"),
    Map(title = "Навигатор"),
    Onboarding(title = "Start"),
    Account(title = "Аккаунт")
}

@Composable
fun ScheduleAppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    groups: List<Group>?,
    selectedGroupId: Int?,
    onGroupSelected: (Int) -> Unit,
    onNavigateToAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            }

            if (currentScreen.title == "SuaiUI") {
                Image(
                    painter = painterResource(Res.drawable.SualBar),
                    contentDescription = "SuaiUI Logo",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(70.dp)
                )
                groups?.let {
                    ExpandableGroupField(
                        items = it,
                        label = "Группа",
                        selectedGroupId = selectedGroupId,
                        onItemSelected = { id -> onGroupSelected(id) }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onNavigateToAccount) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Account",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(90.dp)
                    )
                }
            } else {
                Text(currentScreen.title, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}



@Composable
fun ScheduleApp(
    settingsProvider: SettingsProvider,
    viewModel: ScheduleViewModel = viewModel { ScheduleViewModel(settingsProvider) },
    navController: NavHostController = rememberNavController(),
    mapViewModel: MapViewModel = viewModel { MapViewModel() },
    calendarViewModel: CalendarViewModel = viewModel { CalendarViewModel() },
    noteViewmodel: NoteViewModel = viewModel { NoteViewModel() },
    authViewModel: AuthViewModel = viewModel { AuthViewModel() },
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

    LaunchedEffect(isOnboardingCompleted, navController) {
        navController.currentBackStackEntryFlow.collect { backStack ->
            if (!isOnboardingCompleted && backStack.destination.route != AppScreen.Onboarding.name) {
                navController.navigate(AppScreen.Onboarding.name) {
                    popUpTo(AppScreen.Main.name) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (!isSplashScreenVisible) {
                ScheduleAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    groups = groups,
                    selectedGroupId = viewModel.selectedGroupId,
                    onGroupSelected = { id -> viewModel.selectGroup(id) },
                    onNavigateToAccount = {
                        navController.navigate(AppScreen.Account.name) // Navigate to the account screen
                    },
                    modifier = Modifier.animateContentSize(animationSpec = tween(durationMillis = 300))
                )
            }
        }
    ) { innerPadding ->
        Crossfade(targetState = currentScreen, animationSpec = tween(500)) { screen ->
            NavHost(
                navController = navController,
                startDestination = if (isOnboardingCompleted) AppScreen.Main.name else AppScreen.Onboarding.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp,innerPadding.calculateTopPadding() + 4.dp, 4.dp, 4.dp)
            ) {
                composable(route = AppScreen.Main.name) {
                    Overview(
                        viewModel = viewModel,
                        navController = navController,
                        mapViewModel = mapViewModel,
                        calendarViewModel = calendarViewModel,
                        noteViewModel = noteViewmodel,
                        authViewmodel = authViewModel,
                        onSplashScreenVisibilityChanged = { isVisible ->
                            isSplashScreenVisible = isVisible
                        }
                    )
                }
                composable(route = AppScreen.Onboarding.name) {
                    OnboardingScreen(
                        onComplete = {
                            viewModel.completeOnboarding()
                            navController.navigate(AppScreen.Main.name) {
                                popUpTo(AppScreen.Onboarding.name) { inclusive = true }
                            }
                        }
                    )
                }
                composable(route = AppScreen.Map.name) {
                    MapPage(navController, mapViewModel)
                }
                composable(route = AppScreen.Account.name) {
                    AccountPage(navController, authViewModel) // Add the Account page composable
                }
            }
        }
    }
}







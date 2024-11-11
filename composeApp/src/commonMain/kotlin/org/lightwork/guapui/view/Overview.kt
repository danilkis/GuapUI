package org.lightwork.guapui.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.datetime.isoDayNumber
import org.lightwork.guapui.elements.DayCard
import org.lightwork.guapui.elements.ShimmeringCard
import org.lightwork.guapui.elements.calendar.CalendarSlider
import org.lightwork.guapui.viewmodel.*

@Composable
fun Overview(
    viewModel: ScheduleViewModel,
    navController: NavController,
    mapViewModel: MapViewModel,
    onSplashScreenVisibilityChanged: (Boolean) -> Unit,
    calendarViewModel: CalendarViewModel,
    noteViewModel: NoteViewModel,
    authViewmodel: AuthViewModel
) {
    // Observe the selected date from the CalendarViewModel
    val selectedDate by calendarViewModel.selectedDate.collectAsState()

    val groups by viewModel.groups.collectAsState()
    val weekInfo by viewModel.weekInfo.collectAsState()
    val lessons by viewModel.lessons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSplashScreenVisible = groups == null || weekInfo == null
    onSplashScreenVisibilityChanged(isSplashScreenVisible)

    LaunchedEffect(selectedDate, viewModel.selectedGroupId) {
        selectedDate?.let {
            viewModel.selectDate(selectedDate!!)
            viewModel.loadLessons()  // Передаем выбранную дату
        }
    }

    // Get day names for comparison
    val dayNames = mapOf(
        1 to "Понедельник",
        2 to "Вторник",
        3 to "Среда",
        4 to "Четверг",
        5 to "Пятница",
        6 to "Суббота",
        7 to "Воскресенье"
    )
    val selectedDayName = selectedDate?.dayOfWeek?.isoDayNumber?.let { dayNames[it] }
    println(selectedDayName)

    // Filter lessons by the selected day name
    val filteredLessons = lessons?.filter { it.dayName == selectedDayName }

    Column(Modifier.background(MaterialTheme.colorScheme.surfaceContainer)) {
        // Splash screen visibility
        AnimatedVisibility(
            visible = isSplashScreenVisible,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            val infiniteTransition = rememberInfiniteTransition()
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                )
            )
            SplashScreen()
        }

        val uriHandler = LocalUriHandler.current
        var expanded by remember { mutableStateOf(false) }

        // Main content visibility
        AnimatedVisibility(
            visible = groups != null && weekInfo != null,
            enter = slideInHorizontally(animationSpec = tween(300)) + fadeIn(),
            exit = slideOutHorizontally(animationSpec = tween(300)) + fadeOut()
        ) {
            CalendarSlider(viewModel = calendarViewModel) // Pass the CalendarViewModel to CalendarSlider
        }

        Crossfade(targetState = isLoading) { loading ->
            if (loading) {
                // Show the shimmer loader
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(400.dp),
                    modifier = Modifier.alpha(1f) // Keep shimmer visible
                ) {
                    items(1)
                    {
                        ShimmeringCard()
                    }
                }
            }
            else {
                // Show actual content when loading is done
                if (filteredLessons.isNullOrEmpty()) {
                    // Display the "No lessons" message
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "На этот день пар нет, ура!",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .align(Alignment.Center)  // Center text horizontally and vertically
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(400.dp),
                        modifier = Modifier.alpha(1f) // No fading required as it’s the final state
                    ) {
                        items(filteredLessons) { lessonDay ->
                            DayCard(lessonDay.lessons, lessonDay.dayName, navController, mapViewModel, scheduleViewModel = viewModel, calendarViewModel = calendarViewModel, noteViewModel = noteViewModel, authViewModel = authViewmodel)
                        }
                    }
                }
            }
        }
    }
}


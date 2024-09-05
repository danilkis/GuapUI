package org.lightwork.guapui.view

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow

import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.guap02.screen.element.ExpandableWeekField
import org.lightwork.guapui.elements.DayCard
import org.lightwork.guapui.elements.ExpandableGroupField
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.DpOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import guapui.composeapp.generated.resources.Guap_logo
import guapui.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import org.lightwork.guapui.viewmodel.ScheduleViewModel

@Composable
fun Overview(viewModel: ScheduleViewModel = viewModel { ScheduleViewModel() }) {

    val groups by viewModel.groups.collectAsState()
    val weekInfo by viewModel.weekInfo.collectAsState()
    val lessons by viewModel.lessons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    //val days = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье", "Авто")
    val weekTypes = listOf("Авто", "Числитель", "Знаменатель")
    val scrollFilterState = rememberScrollState()

    Column(Modifier.background(MaterialTheme.colorScheme.background)) {

        // Splash screen visibility
        AnimatedVisibility(
            visible = groups == null || weekInfo == null,
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
//                     Image(
//                         painter = painterResource(Res.drawable.Guap_logo),
//                         contentDescription = "GUAP Logo",
//                         modifier = Modifier
//                             .size(120.dp)
//                             .scale(scale),
//                         contentScale = ContentScale.Crop
//                     )
//                    Spacer(modifier = Modifier.height(24.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .width(350.dp)
                            .height(20.dp)
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        val uriHandler = LocalUriHandler.current
        var expanded by remember { mutableStateOf(false) }

        // Main content visibility
        AnimatedVisibility(
            visible = groups != null && weekInfo != null,
            enter = slideInHorizontally(animationSpec = tween(300)) + fadeIn(),
            exit = slideOutHorizontally(animationSpec = tween(300)) + fadeOut()
        ) {
            LazyRow(
                modifier = Modifier
                    .scrollable(orientation = Orientation.Horizontal, state = scrollFilterState)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    groups?.let {
                        ExpandableGroupField(it, "Группа", onItemSelected = { id ->
                            viewModel.selectGroup(id)
                        })
                    }
                    ExpandableWeekField(weekTypes, "Тип недели", onItemSelected = { type ->
                        viewModel.selectWeekType(type)
                    })

                    Column {
                        OutlinedButton(
                            onClick = { expanded = !expanded },
                        ) {
                            Text("Навигация по корпусам")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            // Ensure that the dropdown menu is anchored to the button
                            offset = DpOffset(x = 0.dp, y = 0.dp) // Adjust offset if necessary
                        ) {
                            DropdownMenuItem(onClick = {
                                uriHandler.openUri("https://guap.ru/map_gast")
                                expanded = false
                            }) {
                                Text("Гастелло")
                            }
                            DropdownMenuItem(onClick = {
                                uriHandler.openUri("https://guap.ru/map_lens")
                                expanded = false
                            }) {
                                Text("Ленсовета")
                            }
                            DropdownMenuItem(onClick = {
                                uriHandler.openUri("https://guap.ru/map_bm")
                                expanded = false
                            }) {
                                Text("Большая Морская")
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                )
            }
        }

        val alpha by animateFloatAsState(
            targetValue = if (!isLoading && lessons != null) 1f else 0f,
            animationSpec = tween(durationMillis = 300)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            this@Column.AnimatedVisibility(
                visible = !isLoading && lessons != null,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(400.dp),
                    modifier = Modifier.alpha(alpha)
                ) {
                    items(lessons ?: emptyList()) { day ->
                        DayCard(day.lessons, day.dayName)
                    }
                }
            }
        }
    }
}
package org.lightwork.guapui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.lightwork.guapui.functions.fetchLessonBuildingNaviUrl
import org.lightwork.guapui.functions.fetchLessonRoomNaviUrl
import org.lightwork.guapui.models.Lesson
import org.lightwork.guapui.viewmodel.MapViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LessonEntry(lesson: Lesson, navController: NavController, mapViewModel: MapViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, start = 4.dp, end = 4.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display CircularProgressIndicator if donePercentage is available, otherwise show the lesson number
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(4.dp)
                        .padding(start = 8.dp, end = 8.dp)
                        .size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (lesson.donePercentage != null) {
                        val progress = (lesson.donePercentage ?: 100) / 100f
                        Box(
                            contentAlignment = Alignment.Center, // Ensures all children are centered
                            modifier = Modifier.size(24.dp)
                        ) {
                            // Background progress (remaining percentage)
                            CircularProgressIndicator(
                                progress = {
                                    1f // Full circle
                                },
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary, // Color for the remaining percentage
                                strokeWidth = 2.dp,
                            )
                            // Foreground progress (done percentage)
                            CircularProgressIndicator(
                                progress = {
                                    progress // Actual progress
                                },
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary, // Color for the done percentage
                                strokeWidth = 2.dp,
                            )
                            // Text in the center showing the percentage
                            Text(
                                text = lesson.number.toString(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.Center) // Explicitly ensure the text is centered
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(99.dp)),
                            contentAlignment = Alignment.Center // Ensures text is centered in the circle
                        ) {
                            Text(
                                text = lesson.number.toString(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.Center) // Explicitly ensure the text is centered
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .fillMaxWidth()
                ) {
                    if (lesson.remainingTime != null)
                    {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp), // Add padding to separate time from the next content
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lesson.time,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            lesson.remainingTime?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                    Text(
                        text = lesson.lessonName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = lesson.type,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        lesson.teachers.forEach { teacher ->
                            AssistChip(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Person,
                                        contentDescription = "Teacher icon",
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                },
                                onClick = {},
                                label = { Text(teacher, color = MaterialTheme.colorScheme.onSurface) }
                            )
                        }
                        AssistChip(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Place,
                                    contentDescription = "Place icon",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            },
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        val roomUri = fetchLessonRoomNaviUrl(lesson)
                                        if (!roomUri.isNullOrEmpty()) {
                                            mapViewModel.setUri(roomUri) // Save the URI into the ViewModel using StateFlow
                                            val platform = getPlatform()
                                            when {
                                                "Java" in platform.name || "Android" in platform.name -> {
                                                    navController.navigate("Map")
                                                }
                                                "Web" in platform.name -> {
                                                    uriHandler.openUri(roomUri)
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        println("Error fetching building uri: ${e.message}")
                                    }
                                }
                            },
                            label = {
                                Text(
                                    lesson.room,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )
                        AssistChip(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Home,
                                    contentDescription = "Home icon",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            },
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        val buildingUri = fetchLessonBuildingNaviUrl(lesson)
                                        if (!buildingUri.isNullOrEmpty())
                                        {
                                           mapViewModel.setUri(buildingUri) // Save the URI into the ViewModel using StateFlow
                                            val platform = getPlatform()
                                            when {
                                                "Java" in platform.name || "Android" in platform.name -> {
                                                    navController.navigate("Map")
                                                }
                                                "Web" in platform.name -> {
                                                    uriHandler.openUri(buildingUri)
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        println("Error fetching building uri: ${e.message}")
                                    }
                                }
                            },
                            label = { Text(lesson.building, color = MaterialTheme.colorScheme.onSurface) }
                        )
                    }

                    // Display the break information if it exists and should be shown
                    if (lesson.showBreak && lesson.breakTime != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth() // Reduce padding at the bottom
                        ) {
                            HorizontalDivider(
                                Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.primaryContainer
                            )
                            Text(
                                text = lesson.breakTime ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 8.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                            )
                        }
                    }
                }
            }
        }
    }
}
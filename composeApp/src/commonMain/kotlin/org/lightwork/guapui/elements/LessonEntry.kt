package org.lightwork.guapui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import org.lightwork.guapui.models.Lesson
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LessonEntry(lesson: Lesson) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer)) {
            Row(
                Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(4.dp)
                        .padding(start = 8.dp, end = 8.dp)
                        .size(24.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(99.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lesson.number.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = lesson.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            onClick = {},
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
                            onClick = {},
                            label = { Text(lesson.building, color = MaterialTheme.colorScheme.onSurface) }
                        )
                    }

                    // Display the break information if it exists and should be shown
                    if (lesson.showBreak && lesson.breakTime != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
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
                                    .padding(horizontal = 8.dp) // Optional: Add some padding around the text
                            )
                        }
                    }
                }
            }
        }
    }
}
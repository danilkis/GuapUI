package org.lightwork.guapui.elements

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.lightwork.guapui.models.Lesson

@Composable
fun LessonEntry(lesson: Lesson) {
    Surface {
        Column(Modifier.fillMaxWidth().padding(8.dp)) {

            /*
            if (lesson.showBreak) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(
                        Modifier.weight(1f),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    Text(
                        text = "Перерыв ${lesson.breakTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    )
                    HorizontalDivider(
                        Modifier.weight(1f),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
            */
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
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
                    Modifier
                        .padding(4.dp)
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
                        fontWeight = FontWeight.SemiBold
                    )
                    Row {
                        Text(
                            text = lesson.room,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = lesson.type,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    lesson.teachers.forEach { teacher ->
                        AssistChip(leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Teacher icon",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }, modifier = Modifier.padding(4.dp), onClick = {}, label = { Text(teacher) })
                    }
                    AssistChip(leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Home,
                            contentDescription = "Home icon",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }, modifier = Modifier.padding(4.dp), onClick = {}, label = { Text(lesson.building) })
                    HorizontalDivider(
                        Modifier.weight(1f),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}
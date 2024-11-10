package org.lightwork.guapui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.lightwork.guapui.functions.fetchLessonBuildingNaviUrl
import org.lightwork.guapui.functions.fetchLessonRoomNaviUrl
import org.lightwork.guapui.models.Lesson
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.lightwork.guapui.models.Note
import org.lightwork.guapui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LessonEntry(
    lesson: Lesson,
    navController: NavController,
    mapViewModel: MapViewModel,
    scheduleViewModel: ScheduleViewModel,
    calendarViewModel: CalendarViewModel,
    noteViewModel: NoteViewModel,
    authViewModel: AuthViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    // State для управления нижним листом
    var noteTitle by remember { mutableStateOf(TextFieldValue()) }
    var isViewNoteDialogOpen by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf(TextFieldValue()) }
    var hasNote by remember { mutableStateOf(false) }
    var existingNote by remember { mutableStateOf<Note?>(null) }
    var isDialogOpen by remember { mutableStateOf(false) }
    val authStatus by authViewModel.authStatus.collectAsState()

    if (authStatus == AuthStatus.AUTHENTICATED) {
        LaunchedEffect(calendarViewModel.selectedDate, lesson.number, scheduleViewModel.selectedGroupId) {
            calendarViewModel.selectedDate?.let { date ->
                date.value?.let {
                    noteViewModel.getFilteredNotes(
                        group = scheduleViewModel.selectedGroupId.toString(),
                        date = it,
                        lessonNumber = lesson.number
                    ) { notes ->
                        if (notes.isNotEmpty()) {
                            hasNote = true
                            existingNote = notes.first()
                        } else {
                            hasNote = false
                            existingNote = null
                        }
                    }
                }
            }
        }
    }

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
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(24.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = 1f,
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                            )
                            CircularProgressIndicator(
                                progress = progress,
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp,
                            )
                            Text(
                                text = lesson.number.toString(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(99.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lesson.number.toString(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.Center)
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
                    if (lesson.remainingTime != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
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
                                            mapViewModel.setUri(roomUri)
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
                                        if (!buildingUri.isNullOrEmpty()) {
                                            mapViewModel.setUri(buildingUri)
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
                        if (authStatus == AuthStatus.AUTHENTICATED) {
                            AssistChip(
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (hasNote) Icons.Rounded.MailOutline else Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                              },
                                onClick = {
                                    if (hasNote) {
                                        // Открытие диалога для просмотра заметки
                                        isViewNoteDialogOpen = true
                                    } else {
                                        // Открытие диалога для добавления новой заметки
                                        isDialogOpen = true
                                    }
                                          },
                                label = {
                                    Text(
                                        if (hasNote) "Прочитать" else "Добавить",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            title = { Text("Добавить заметку", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Заголовок") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Заметка") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Создание и сохранение заметки
                    val newNote = calendarViewModel.selectedDate?.let {
                        it.value?.let { it1 ->
                            Note(
                                id = "",
                                created_at = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                                lessonNumber = lesson.number,
                                date = it1,
                                text = noteText.text,
                                user_uuid = "", // Добавьте ID пользователя
                                group = scheduleViewModel.selectedGroupId.toString(),
                                title = noteTitle.text
                            )
                        }
                    }
                    newNote?.let { noteViewModel.saveNote(it) }

                    // Обновление состояния сразу после сохранения заметки
                    hasNote = true
                    existingNote = newNote // Сохраняем только что добавленную заметку
                    isDialogOpen = false
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { isDialogOpen = false }) {
                    Text("Отмена")
                }
            }
        )
    }
if (isViewNoteDialogOpen && existingNote != null) {
    AlertDialog(
        onDismissRequest = { isViewNoteDialogOpen = false },
        title = { Text("Заметка: ${existingNote?.title}", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Создано: ${existingNote?.created_at}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = existingNote?.text ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { isViewNoteDialogOpen = false }) {
                Text("Закрыть")
            }
        },
        dismissButton = { // This is the new button for deleting the note
            TextButton(
                onClick = {
                    existingNote?.id?.let { noteId ->
                        noteViewModel.deleteNote(noteId)
                                // Handle successful deletion
                                isViewNoteDialogOpen = false
                                // Update state to reflect that there's no note anymore
                                hasNote = false
                                existingNote = null
                        }
                    }
            ) {
                Text("Удалить")
            }
        }
    )
}
}
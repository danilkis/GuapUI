package org.lightwork.guapui

import androidx.compose.foundation.BorderStroke
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

    val teritaryChipColor = ChipColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer,
        MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.surfaceDim, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)

    val secondaryChipColors = ChipColors(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.surfaceDim, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)

    val primaryChipColors = ChipColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimaryContainer, MaterialTheme.colorScheme.surfaceDim, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)


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
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Box(
//                    modifier = Modifier
//                        .background(MaterialTheme.colorScheme.surfaceContainer)
//                        .padding(4.dp)
//                        .padding(start = 8.dp, end = 8.dp)
//                        .size(24.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    if (lesson.donePercentage != null) {
//                        val progress = (lesson.donePercentage ?: 100) / 100f
//                        Box(
//                            contentAlignment = Alignment.Center,
//                            modifier = Modifier.size(24.dp)
//                        ) {
//                            CircularProgressIndicator(
//                                progress = { 1f },
//                                modifier = Modifier.size(24.dp),
//                                color = MaterialTheme.colorScheme.onPrimary,
//                                strokeWidth = 2.dp,
//                            )
//                            CircularProgressIndicator(
//                                progress = { progress },
//                                modifier = Modifier.size(24.dp),
//                                color = MaterialTheme.colorScheme.primary,
//                                strokeWidth = 2.dp,
//                            )
//                            Text(
//                                text = lesson.number.toString(),
//                                style = MaterialTheme.typography.labelLarge,
//                                color = MaterialTheme.colorScheme.primary,
//                                modifier = Modifier.align(Alignment.Center)
//                            )
//                        }
//                    } else {
//                        Box(
//                            modifier = Modifier
//                                .size(24.dp)
//                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(99.dp)),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = lesson.number.toString(),
//                                style = MaterialTheme.typography.labelLarge,
//                                color = MaterialTheme.colorScheme.primary,
//                                modifier = Modifier.align(Alignment.Center)
//                            )
//                        }
//                    }
//                }

                Column(
                    modifier = Modifier
                        //.background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(8.dp)
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
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            lesson.remainingTime?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                    Text(
                        text = lesson.number.toString() + ". " + lesson.lessonName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = lesson.type,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AssistChip(
                           colors = primaryChipColors,
                            //                            leadingIcon = {
//                                Icon(
//                                    imageVector = Icons.Rounded.Place,
//                                    contentDescription = "Place icon",
//                                    tint = MaterialTheme.colorScheme.tertiary
//                                )
//                            },
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
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )
                        AssistChip(
                            colors = teritaryChipColor,
//                            leadingIcon = {
//                                Icon(
//                                    imageVector = Icons.Rounded.Home,
//                                    contentDescription = "Home icon",
//                                    tint = MaterialTheme.colorScheme.tertiary
//                                )
//                            },
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
                            label = { Text(lesson.building, color = MaterialTheme.colorScheme.onTertiaryContainer) }
                        )
                        lesson.teachers.forEach { teacher ->
                            AssistChip(
                               colors = teritaryChipColor,
//                                leadingIcon = {
//                                    Icon(
//                                        imageVector = Icons.Rounded.Person,
//                                        contentDescription = "Teacher icon",
//                                        tint = MaterialTheme.colorScheme.tertiary
//                                    )
//                                },
                                onClick = {},
                                label = { Text(teacher, color = MaterialTheme.colorScheme.onTertiaryContainer) }
                            )
                        }
                        if (authStatus == AuthStatus.AUTHENTICATED) {
                            AssistChip(
                                colors = secondaryChipColors,
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (hasNote) Icons.Rounded.MailOutline else Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
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
                                        if (hasNote) "" else "",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }

                }
            }
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
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Card(border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), modifier = Modifier.align(
                        Alignment.Center), colors = CardColors(containerColor = MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outline))
                    {
                        Text(
                            text = lesson.breakTime ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(4.dp)
                                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                        )
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
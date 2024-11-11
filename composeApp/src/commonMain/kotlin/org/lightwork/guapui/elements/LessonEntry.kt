package org.lightwork.guapui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.TextFieldColors
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
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material.OutlinedRichTextEditor
import com.mohamedrejeb.richeditor.ui.material.RichText
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.lightwork.guapui.elements.editor.RichEditorContent
import org.lightwork.guapui.elements.editor.RichTextStyleRow
import org.lightwork.guapui.models.Note
import org.lightwork.guapui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalMaterialApi::class,
    ExperimentalRichTextApi::class
)
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
    var noteText by remember { mutableStateOf(TextFieldValue()) }
    var hasNote by remember { mutableStateOf(false) }
    var existingNote by remember { mutableStateOf<Note?>(null) }
    var isNoteSheetVisible by remember { mutableStateOf(false) }
    var isViewNoteSheetVisible by remember { mutableStateOf(false) }
    var dataLoading by remember { mutableStateOf(true) }
    val authStatus by authViewModel.authStatus.collectAsState()
    val EditSheetState = rememberModalBottomSheetState()
    val ViewSheetState = rememberModalBottomSheetState()
    val richTextState = rememberRichTextState()
    val outlinedRichTextState = rememberRichTextState()

    val teritaryChipColor = ChipColors(
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.onTertiaryContainer,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.surfaceDim,
        MaterialTheme.colorScheme.onSurfaceVariant,
        MaterialTheme.colorScheme.onSurfaceVariant,
        MaterialTheme.colorScheme.onSurfaceVariant
    )

    val secondaryChipColors = ChipColors(
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.surfaceDim,
        MaterialTheme.colorScheme.onSurfaceVariant,
        MaterialTheme.colorScheme.onSurfaceVariant,
        MaterialTheme.colorScheme.onSurfaceVariant
    )

    val primaryChipColors = ChipColors(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.surfaceDim,
        MaterialTheme.colorScheme.onSurfaceVariant,
        MaterialTheme.colorScheme.onSurfaceVariant,
        MaterialTheme.colorScheme.onSurfaceVariant
    )

    LaunchedEffect(calendarViewModel.selectedDate, lesson.number, scheduleViewModel.selectedGroupId) {
        if (authStatus == AuthStatus.AUTHENTICATED) {
            calendarViewModel.selectedDate?.let { date ->
                date.value?.let {
                    // Collect from the flow returned by getFilteredNotes
                    noteViewModel.getFilteredNotes(
                        group = scheduleViewModel.selectedGroupId.toString(),
                        date = it,
                        lessonNumber = lesson.number
                    ).collect { notes ->
                        if (notes.isNotEmpty()) {
                            hasNote = true
                            existingNote = notes.first() // Update the existing note
                        } else {
                            hasNote = false
                            existingNote = null
                        }
                        dataLoading = false
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
                Column(
                    modifier = Modifier
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
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AssistChip(
                            colors = primaryChipColors,
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
                                        isViewNoteSheetVisible = true
                                    } else {
                                        isNoteSheetVisible = true
                                    }
                                },
                                label = {
                                    //Text("", color = MaterialTheme.colorScheme.onSurface) // No text, just icon
                                }
                            )
                        }
                    }
                }
            }
            if (lesson.showBreak && lesson.breakTime != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    HorizontalDivider(
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Card(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.align(
                            Alignment.Center
                        ),
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.colorScheme.outline
                        )
                    )
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

    @Suppress("DEPRECATION")
    val textFieldColors2: androidx.compose.material.TextFieldColors =
        androidx.compose.material.TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.12f),
            cursorColor = MaterialTheme.colorScheme.primary,
            errorCursorColor = MaterialTheme.colorScheme.error,

            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            errorIndicatorColor = MaterialTheme.colorScheme.error,

            leadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            errorLeadingIconColor = MaterialTheme.colorScheme.error,

            trailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            errorTrailingIconColor = MaterialTheme.colorScheme.error,

            focusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            errorLabelColor = MaterialTheme.colorScheme.error,

            placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    if (isNoteSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { isNoteSheetVisible = false },
            sheetState = EditSheetState,
        ) {
            Column(
                modifier = Modifier.padding(16.dp).windowInsetsPadding(WindowInsets.ime)
                    .fillMaxWidth()
            ) {
                Text("Редактирование заметки", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    label = { Text("Заголовок") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                RichTextStyleRow(
                    modifier = Modifier.fillMaxWidth(),
                    state = outlinedRichTextState,
                )
                OutlinedRichTextEditor(
                    modifier = Modifier.fillMaxWidth(),
                    state = outlinedRichTextState,
                    colors = textFieldColors2,
                    maxLines = 20,
                    minLines = 3
                )
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { isNoteSheetVisible = false }) {
                        Text("Отменить")
                    }
                    TextButton(onClick = {
                        // Saving new note
                        val newNote = calendarViewModel.selectedDate?.let {
                            it.value?.let { it1 ->
                                Note(
                                    id = "",
                                    created_at = Clock.System.now().toLocalDateTime(TimeZone.UTC),
                                    title = noteTitle.text,
                                    text = outlinedRichTextState.toHtml(),
                                    user_uuid = "",
                                    group = scheduleViewModel.selectedGroupId.toString(),
                                    lessonNumber = lesson.number,
                                    date = it1
                                )
                            }
                        }
                        isViewNoteSheetVisible = false
                        newNote?.let {
                            noteViewModel.saveNote(newNote)
                            hasNote = true // Set the state to show that the note was added
                            isNoteSheetVisible = false
                        }
                    }) {
                        Text("Готово")
                    }
                }
            }
        }
    }


    if (isViewNoteSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { isViewNoteSheetVisible = false },
            sheetState = ViewSheetState,
        ) {

            Column(modifier = Modifier.padding(16.dp)) {
                Text(existingNote?.title ?: "", style = MaterialTheme.typography.titleLarge)
                richTextState.setHtml(existingNote?.text ?: "")
                RichText(
                    richTextState,
                    modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { isViewNoteSheetVisible = false }) {
                        Text("Закрыть")
                    }
                    TextButton(onClick = {
                        existingNote?.let { note ->
                            noteTitle = TextFieldValue(note.title)
                            isNoteSheetVisible = true  // Open edit sheet
                        }
                        existingNote?.text?.let { outlinedRichTextState.setHtml(it) }
                    }) {
                        Text("Редактировать")
                    }
                    TextButton(onClick = {
                        existingNote?.let { noteViewModel.deleteNote(it.id) }
                        isViewNoteSheetVisible = false
                        hasNote = false // Remove the note
                    }) {
                        Text("Удалить")
                    }
                }
            }
        }
    }
}
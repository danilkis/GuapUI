package org.lightwork.guapui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.lightwork.guapui.models.Note
import org.lightwork.guapui.helper.SupabaseHelper
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class NoteViewModel(public val supabaseHelper: SupabaseHelper) : ViewModel() {
    private var jwtToken: String? = null

    // State for note dialog, selected lesson, etc.

    // Функция для получения отфильтрованных заметок по group, date и lessonNumber

    fun getFilteredNotes(
        group: String,
        date: LocalDate,
        lessonNumber: Int
    ): Flow<List<Note>> {
        return flow {
            while (true) {
                try {
                    val notes = supabaseHelper.base.from("notes").select {
                        filter {
                            eq("group", group)
                            eq("date", date.toString()) // Convert date to string for the query
                            eq("lessonNumber", lessonNumber)
                        }
                    }.decodeList<Note>() // Decode the response as a list of notes

                    emit(notes) // Emit the notes to the Flow
                } catch (e: Exception) {
                    e.printStackTrace()
                    //emit(emptyList()) // Emit an empty list in case of an error
                }
                delay(700) // Wait for 2 seconds before fetching again
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            try {
                val result = supabaseHelper.base.from("notes").delete { filter { eq("id", noteId) } }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Function to save the note to Supabase
    @OptIn(ExperimentalUuidApi::class)
    fun saveNote(
        note: Note = Note(
            "",
            Clock.System.now().toLocalDateTime(TimeZone.UTC),
            1,
            LocalDate.parse("2024-11-12"),
            "This is a test",
            "",
            "4441",
            "Test Title"
        )
    ) {
        viewModelScope.launch {
            try {
                val newNoteId = if (note.id.isBlank()) Uuid.random().toString() else note.id
                val userUuid = supabaseHelper.base.auth.retrieveUser(
                    supabaseHelper.base.auth.currentAccessTokenOrNull().toString()
                ).id // Retrieve the user UUID

                // Check if the note already exists with the same group, date, and lesson number
                val existingNote = supabaseHelper.base.from("notes").select {
                    filter {
                        eq("group", note.group)
                        eq("date", note.date.toString()) // Convert date to string
                        eq("lessonNumber", note.lessonNumber)
                    }
                }.decodeList<Note>().firstOrNull()

                if (existingNote != null) {
                    // If the note exists, update it
                    val updatedResult = supabaseHelper.base.from("notes").update(
                        Note(
                            newNoteId, note.created_at, note.lessonNumber, note.date, note.text,
                            userUuid, note.group, note.title
                        )
                    )
                    {
                        filter {
                            eq("id", existingNote.id)
                        }
                    }
                } else {
                    val result = supabaseHelper.base.from("notes").upsert(
                        Note(
                            newNoteId, note.created_at, note.lessonNumber, note.date, note.text,
                            userUuid, note.group, note.title
                        )
                    ).decodeList<Note>()

                    if (result.isNotEmpty()) {
                        println("Note added successfully")
                    } else {
                        println("No result returned")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

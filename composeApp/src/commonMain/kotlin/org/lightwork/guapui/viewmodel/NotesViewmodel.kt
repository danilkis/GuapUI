package org.lightwork.guapui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.lightwork.guapui.models.Note
import org.lightwork.guapui.helper.SupabaseHelper
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid



class NoteViewModel : ViewModel() {
    private val supabaseHelper = SupabaseHelper(
        supabaseUrl = "https://vjfdmvrkriajftklozgf.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZqZmRtdnJrcmlhamZ0a2xvemdmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzA4MzYyMTUsImV4cCI6MjA0NjQxMjIxNX0.RgDjBvwNacNVLK2vqCt-2i-0kx6MaSKEUUWokfc_fcc"
    )

    private var jwtToken: String? = null

    // State for note dialog, selected lesson, etc.

    // Функция для получения отфильтрованных заметок по group, date и lessonNumber
    fun getFilteredNotes(
        group: String,
        date: LocalDate,
        lessonNumber: Int,
        onResult: (List<Note>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val notes = supabaseHelper.base.from("notes").select{
                    filter{
                        eq("group", group)
                        eq("date", date.toString()) // Преобразуем дату в строку для запроса
                        eq("lessonNumber", lessonNumber)
                    }
                }
                    .decodeList<Note>() // Декодируем ответ как список Note

                onResult(notes) // Возвращаем отфильтрованный список через callback
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(emptyList()) // Возвращаем пустой список в случае ошибки
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            try {
                val result = supabaseHelper.base.from("notes").delete { filter { eq("id", noteId) }}
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
                // Use the JWT token for the request
                println(
                    mapOf(
                        "id" to Uuid.random().toString(),
                        "created_at" to note.created_at,
                        "lessonNumber" to note.lessonNumber,
                        "date" to note.date.toString(),
                        "text" to note.text,
                        "user_uuid" to supabaseHelper.base.auth.retrieveUser(
                            supabaseHelper.base.auth.currentAccessTokenOrNull()
                                .toString()
                        ).id,  // Or pass the user UUID if required
                        "group" to note.group,
                        "title" to note.title
                    ).toString()
                )
                val result = supabaseHelper.base.from("notes").insert(
                    Note(
                        Uuid.random().toString(), note.created_at, note.lessonNumber, note.date, note.text,
                        supabaseHelper.base.auth.retrieveUser(
                            supabaseHelper.base.auth.currentAccessTokenOrNull().toString()
                        ).id,
                        note.group, note.title
                    )
                ).decodeList<Note>() // Using decodeList if you expect an array
                if (result.isNotEmpty()) {
                    println("Note added successfully")
                } else {
                    println("No result returned")
                }
                println(result) // Log the response to inspect it
                // Handle result (e.g., success message or further processing)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

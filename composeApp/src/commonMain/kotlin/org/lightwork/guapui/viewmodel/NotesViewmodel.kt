package org.lightwork.guapui.viewmodel

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.LocalDate

class NotesViewModel(private val supabaseClient: SupabaseClient) {

    // Function to create a new note for a specific lesson on a given date
    suspend fun createNote(
        lessonNumber: Int,
        date: LocalDate,
        text: String,
        userUuid: String
    ) {
        val notesTable = supabaseClient.from("notes") // Assuming 'notes' is the name of your table

        val data = mapOf(
            "lessonNumber" to lessonNumber,
            "date" to date,
            "text" to text,
            "user_uuid" to userUuid
        )

        // Insert the note into the database
        //val response = notesTable.insert(data).execute()

//        if (response.error != null) {
//            throw Exception("Error creating note: ${response.error.message}")
//        }
    }

    // Function to fetch all notes for a specific day
    /*
    suspend fun fetchNotesForDay(date: LocalDate): List<Note> {
        val notesTable = supabaseClient.from("notes") // Assuming 'notes' is the name of your table

        val response = notesTable.select("*").eq("date", date).execute()

        if (response.error != null) {
            throw Exception("Error fetching notes: ${response.error.message}")
        }

        return response.data?.map {
            Note(
                id = it["id"] as Int,
                createdAt = it["created_at"] as String,
                lessonNumber = it["lessonNumber"] as Int,
                date = it["date"] as String,
                text = it["text"] as String,
                userUuid = it["user_uuid"] as UUID
            ) ?: emptyList()
        } ?: emptyList()
    }

     */
}

// Data class for Notes
data class Note(
    val id: Int,
    val createdAt: String,
    val lessonNumber: Int,
    val date: String,
    val text: String,
    val userUuid: String
)

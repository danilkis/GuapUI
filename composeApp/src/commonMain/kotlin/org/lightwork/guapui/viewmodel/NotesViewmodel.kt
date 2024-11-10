import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.datetime.LocalDate
import org.lightwork.guapui.models.Lesson
import org.lightwork.guapui.models.Note

class NoteViewModel : ViewModel() {

    // State to track whether the note dialog is open
    private val _isNoteDialogOpen = MutableStateFlow(false)
    val isNoteDialogOpen: StateFlow<Boolean> = _isNoteDialogOpen

    // State to track the selected lesson for which the note is being created
    private val _selectedLesson = MutableStateFlow<Lesson?>(null)
    val selectedLesson: StateFlow<Lesson?> = _selectedLesson

    // State to track the current note text
    private val _noteText = MutableStateFlow("")
    val noteText: StateFlow<String> = _noteText

    // Function to open the note dialog for a selected lesson
    fun openNoteDialog(lesson: Lesson) {
        _selectedLesson.value = lesson
        _isNoteDialogOpen.value = true
    }

    // Function to close the note dialog
    fun closeNoteDialog() {
        _isNoteDialogOpen.value = false
        _selectedLesson.value = null
        _noteText.value = ""
    }

    // Function to update the note text as the user types
    fun updateNoteText(newText: String) {
        _noteText.value = newText
    }

    // Function to save the note in Supabase (or other backend)
    fun saveNoteToDatabase() {
        val lesson = _selectedLesson.value ?: return
        val note = _noteText.value

        viewModelScope.launch {
            try {
                // Call Supabase API to save the note (or your backend logic)
                //saveNoteToSupabase(Note(null, lesson.number, date, note, null, group))

                // Once saved, close the dialog
                closeNoteDialog()
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    // This is just a placeholder function for the actual saving logic to Supabase
    //TODO: Тест на добавление записи
    public suspend fun saveNoteToSupabase(note: Note = Note(0, 1, LocalDate.parse("2024-11-12"),"This is a test", "7f56e6b9-1e27-4ab8-ae9e-3623348cfa66", "4441" )) {
        val db = createSupabaseClient(supabaseUrl = "https://vjfdmvrkriajftklozgf.supabase.co", supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZqZmRtdnJrcmlhamZ0a2xvemdmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzA4MzYyMTUsImV4cCI6MjA0NjQxMjIxNX0.RgDjBvwNacNVLK2vqCt-2i-0kx6MaSKEUUWokfc_fcc")
        {
            install(Auth)
            install(Postgrest)
        }
        val result = db.from("notes").insert(note) {
            select()
        }.decodeSingle<Note>()

    }
}

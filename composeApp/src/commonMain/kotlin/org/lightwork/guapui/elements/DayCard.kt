package org.lightwork.guapui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.lightwork.guapui.LessonEntry
import org.lightwork.guapui.models.Lesson
import org.lightwork.guapui.viewmodel.*

@Composable
fun DayCard(lessons: List<Lesson>?, label: String, navController: NavController, mapViewmodel: MapViewModel, scheduleViewModel: ScheduleViewModel, calendarViewModel: CalendarViewModel, noteViewModel: NoteViewModel, authViewModel: AuthViewModel) {
    if (lessons == null) {  // Show shimmer effect if lessons are null (loading state)
        ShimmeringCard(modifier = Modifier.fillMaxWidth().padding(8.dp))
        return
    }

    Card(
        Modifier.padding(8.dp).shadow(4.dp, shape = MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainer)) {
            Row(Modifier.background(MaterialTheme.colorScheme.primary).fillMaxWidth().padding(8.dp)) {
                Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
            }
            lessons.forEach {
                LessonEntry(it, navController, mapViewmodel, scheduleViewModel, calendarViewModel, noteViewModel, authViewModel = authViewModel)
            }
        }
    }
}

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.guap02.screen.element.ExpandableField
import org.lightwork.guapui.elements.DayCard
import org.lightwork.guapui.functions.fetchLessons
import org.lightwork.guapui.models.Lesson

@Composable
fun Overview() {
    // Определяем состояние для хранения уроков
    var lessons by remember { mutableStateOf<List<Lesson>?>(null) }
    val groups = listOf("Группа 1", "Группа 2", "Группа 3", "Авто")
    val days = listOf(
        "Понедельник",
        "Вторник",
        "Среда",
        "Четверг",
        "Пятница",
        "Суббота",
        "Воскресенье",
        "Авто"
    )
    val weekTypes = listOf("Числитель", "Знаменатель", "Авто")
    val scrollFilterState = rememberScrollState(0)

    // Загружаем данные в suspend-функции
    LaunchedEffect(true) {
        lessons = fetchLessons()
    }

    Column {
        LazyRow(
            modifier = Modifier
                .scrollable(orientation = Orientation.Horizontal, state = scrollFilterState)
        ) {
            item {
                ExpandableField(groups, "Группа")
                ExpandableField(days, "День недели")
                ExpandableField(weekTypes, "Тип недели")
            }
        }

        // Проверяем, загружены ли данные
        if (lessons == null) {
            // Если данные еще не загружены, показываем индикатор
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Если данные загружены, отображаем их
            LazyColumn {
                items(lessons!!.chunked(3)) {
                    DayCard(it, "Понедельник")
                }
            }
        }
    }
}
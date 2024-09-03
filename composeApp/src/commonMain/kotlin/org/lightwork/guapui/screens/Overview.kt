import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.guap02.screen.element.ExpandableWeekField
import guapui.composeapp.generated.resources.Res
import org.lightwork.guapui.elements.DayCard
import org.lightwork.guapui.elements.ExpandableGroupField
import org.lightwork.guapui.functions.fetchGroups
import org.lightwork.guapui.functions.fetchLessons
import org.lightwork.guapui.models.Day
import org.lightwork.guapui.models.Group
import kotlinx.serialization.Serializable
import org.lightwork.guapui.functions.fetchWeekInfo
import org.lightwork.guapui.models.WeekInfo
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import guapui.composeapp.generated.resources.Guap_logo
import guapui.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

fun Boolean.toInt() = if (this) 1 else 2



@Composable
fun Overview() {
    var lessons by remember { mutableStateOf<List<Day>?>(null) }
    var groups by remember { mutableStateOf<List<Group>?>(null) }
    var selectedGroupId by remember { mutableStateOf(0) }
    var selectedWeekType by remember { mutableStateOf("Авто") }
    var weekInfo by remember { mutableStateOf<WeekInfo?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val days = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье", "Авто")
    val weekTypes = listOf("Числитель", "Знаменатель", "Авто")
    val scrollFilterState = rememberScrollState()

    // Load groups data
    LaunchedEffect(Unit) {
        try {
            groups = fetchGroups()
        } catch (e: Exception) {
            println("Error fetching groups: ${e.message}")
        }
    }

    // Load week info data
    LaunchedEffect(Unit) {
        try {
            weekInfo = fetchWeekInfo()
            println("Week info fetched: $weekInfo")
        } catch (e: Exception) {
            println("Error fetching week info: ${e.message}")
        }
    }

    // Update lessons when selectedGroupId or weekInfo changes
    LaunchedEffect(selectedGroupId, selectedWeekType, weekInfo) {
        if (selectedGroupId != 0 && weekInfo != null) {
            isLoading = true
            try {
                val weekNumber = when (selectedWeekType) {
                    "Числитель" -> 1
                    "Знаменатель" -> 2
                    "Авто" -> weekInfo!!.IsWeekOdd.toInt()
                    else -> 1
                }
                lessons = fetchLessons(selectedGroupId, weekNumber)
                println("Lessons fetched for group $selectedGroupId and week $weekNumber")
            } catch (e: Exception) {
                println("Error fetching lessons: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Column(Modifier.background(MaterialTheme.colorScheme.background)) {

        // Splash screen visibility
        AnimatedVisibility(
            visible = groups == null || weekInfo == null,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            val infiniteTransition = rememberInfiniteTransition()

            // Animation values for the pulsating logo
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Guap Logo with pulsating effect
//                    Image(
//                        painter = painterResource(Res.drawable.Guap_logo), // Replace with your image resource
//                        contentDescription = "GUAP Logo",
//                        modifier = Modifier
//                            .size(120.dp)
//                            .scale(scale),
//                        contentScale = ContentScale.Crop
//                    )
//                    Spacer(modifier = Modifier.height(24.dp))

                    // Horizontal progress bar with spinning effect
                    LinearProgressIndicator(
                        modifier = Modifier
                            .width(350.dp)
                            .height(20.dp)
                            .padding(8.dp),
                        color = Color.White
                    )
                }
            }
        }

        // Main content visibility
        AnimatedVisibility(
            visible = groups != null && weekInfo != null,
            enter = slideInHorizontally(animationSpec = tween(300)) + fadeIn(),
            exit = slideOutHorizontally(animationSpec = tween(300)) + fadeOut()
        ) {
            LazyRow(
                modifier = Modifier
                    .scrollable(orientation = Orientation.Horizontal, state = scrollFilterState)
                    .fillMaxWidth()
            ) {
                item {
                    groups?.let {
                        ExpandableGroupField(it, "Группа", onItemSelected = { id ->
                            selectedGroupId = id
                        })
                    }
                    ExpandableWeekField(weekTypes, "Тип недели", onItemSelected = { type ->
                        selectedWeekType = type
                    })
                }
            }
        }

        // Animated visibility for the loading indicator
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material.CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                )
            }
        }

        // Fade in effect for the lessons content
        val alpha by animateFloatAsState(
            targetValue = if (!isLoading && lessons != null) 1f else 0f,
            animationSpec = tween(durationMillis = 300)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            this@Column.AnimatedVisibility(
                visible = !isLoading && lessons != null,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(400.dp),
                    modifier = Modifier.alpha(alpha)
                ) {
                    items(lessons ?: emptyList()) { day ->
                        DayCard(day.lessons, day.dayName)
                    }
                }
            }
        }
    }
}
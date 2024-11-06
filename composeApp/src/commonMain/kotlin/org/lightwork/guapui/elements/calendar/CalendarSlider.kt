package org.lightwork.guapui.elements.calendar

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.lightwork.guapui.viewmodel.CalendarViewModel

import kotlinx.datetime.*
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch

@Composable
fun CalendarSlider(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel
) {
    val dataSource = CalendarDataSource()
    var weekOffset by remember { mutableStateOf(0) }
    var selectedDate by remember { mutableStateOf<CalendarUiModel.Date?>(null) }

    val currentDate = dataSource.today

    // Calculate the month based on the current offset (move by weeks).
    val startDate = currentDate.plus(DatePeriod(weekOffset))

    // Get data for the current month.
    val calendarUiModel = dataSource.getData(startDate = startDate, lastSelectedDate = currentDate)

    if (selectedDate == null) {
        selectedDate = calendarUiModel.visibleDates.find { it.date == currentDate }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column {
        Header(
            data = calendarUiModel,
            weekOffset = weekOffset,
            onPrevClick = {
                weekOffset -= 1 // Navigate 4 weeks back (approx 1 month)
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            },
            onNextClick = {
                weekOffset += 1// Navigate 4 weeks forward
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            }
        )
        Content(
            data = calendarUiModel,
            selectedDate = selectedDate!!,
            onDateSelected = { selectedDate = it; viewModel.onDateSelected(it) },
            listState = listState
        )
    }
}

@Composable
fun Header(
    data: CalendarUiModel,
    weekOffset: Int, // Offset in weeks from today
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    // Calculate the label dynamically
    val headerText = when {
        data.selectedDate.date == currentDate -> "Сегодня"
        weekOffset == 0 -> "Эта неделя"
        weekOffset == 1 -> "Следующая неделя"
        weekOffset > 1 -> "${weekOffset} недель спустя"
        weekOffset == -1 -> "Прошлая неделя"
        else -> "${-weekOffset} недель назад"
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = headerText,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 35.sp
        )
        IconButton(onClick = onPrevClick) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        IconButton(onClick = onNextClick) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun Content(
    data: CalendarUiModel,
    onDateSelected: (CalendarUiModel.Date) -> Unit,
    selectedDate: CalendarUiModel.Date,
    listState: LazyListState
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        state = listState,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(items = data.visibleDates) { date ->
            ContentItem(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
fun ContentItem(
    date: CalendarUiModel.Date,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Crossfade(
        targetState = isSelected,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    ) { selected ->
        Card(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 4.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                }
            ),
            shape = if (selected) {
                MaterialTheme.shapes.large
            } else {
                MaterialTheme.shapes.medium
            },
            elevation = if (selected) {
                CardDefaults.elevatedCardElevation(6.dp)
            } else {
                CardDefaults.elevatedCardElevation(0.dp)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .width(60.dp)
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = date.day,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = date.date.dayOfMonth.toString(),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = if (selected) 20.sp else 16.sp
                    ),
                )
            }
        }
    }
}

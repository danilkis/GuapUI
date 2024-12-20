package org.lightwork.guapui.elements.calendar

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Month
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.lightwork.guapui.viewmodel.CalendarViewModel

@Composable
fun CalendarSlider(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel
) {
    val dataSource = CalendarDataSource()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var weekOffset by remember { mutableStateOf(0) }
    var selectedDate by remember { mutableStateOf<CalendarUiModel.Date?>(null) }
    var displayedMonth by remember { mutableStateOf(dataSource.today.month) }
    var weekInMonth by remember { mutableStateOf(1) }

    val currentDate = dataSource.today
    val startDate = currentDate.plus(DatePeriod(days = weekOffset * 7))

    // Fetch data for three months
    val calendarUiModel = dataSource.getData(
        startDate = startDate.minus(DatePeriod(months = 1)), // One month back for smooth scrolling
        lastSelectedDate = currentDate
    )

    if (selectedDate == null) {
        selectedDate = calendarUiModel.visibleDates.find { it.date == currentDate }
    }

    // Track the month and week based on the first visible item in the LazyRow
    LaunchedEffect(listState.firstVisibleItemIndex) {
        val firstVisibleDate = calendarUiModel.visibleDates.getOrNull(listState.firstVisibleItemIndex)
        if (firstVisibleDate != null) {
            val newMonth = firstVisibleDate.date.month
            if (newMonth == displayedMonth) {
                weekInMonth = (listState.firstVisibleItemIndex / 7) + 1
            } else {
                displayedMonth = newMonth
                weekInMonth = 1
            }
        }
    }

    // Ensure the current date is visible on first load
    LaunchedEffect(calendarUiModel.visibleDates) {
        val currentIndex = calendarUiModel.visibleDates.indexOfFirst { it.date == currentDate }
        if (currentIndex != -1) {
            listState.scrollToItem(currentIndex)  // Scroll to the current date
        }
    }
    Box(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow).fillMaxWidth()) //TODO: Беспонтоыве поля
    {
        Column(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow).fillMaxWidth()) {
            Header(
                displayedMonth = displayedMonth,
                weekInMonth = weekInMonth,
                onPrevClick = {
                    coroutineScope.launch {
                        val targetIndex = (listState.firstVisibleItemIndex - 7).coerceAtLeast(0)
                        listState.animateScrollToItem(targetIndex)
                    }
                },
                onNextClick = {
                    coroutineScope.launch {
                        val targetIndex =
                            (listState.firstVisibleItemIndex + 7).coerceAtMost(calendarUiModel.visibleDates.size - 1)
                        listState.animateScrollToItem(targetIndex)
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
}

fun Month.getDisplayNameInRussian(): String {
    val monthsInRussian = listOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )
    return monthsInRussian[this.ordinal]
}

@Composable
fun Header(
    displayedMonth: Month,
    weekInMonth: Int,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val monthDisplayName = displayedMonth.getDisplayNameInRussian()
    val headerText = monthDisplayName.capitalize()

    Row(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
        Crossfade(targetState = headerText) { text ->
            Text(
                text = "$text",
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 35.sp
            )
        }
        Row(modifier = Modifier.weight(1f)) //TODO: Кнопки на правой части экрана
        {
            IconButton(onClick = onPrevClick) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = onNextClick) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Вперед",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

// Helper function to get the correct plural form of "week" in Russian
fun getWeeksLabel(weeks: Int): String {
    val lastDigit = weeks % 10
    return when {
        weeks in 11..19 -> "недель" // Special case for teens (11-19)
        lastDigit == 1 -> "неделя"
        lastDigit in 2..4 -> "недели"
        else -> "недель"
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
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(items = data.visibleDates) { date ->
            ContentItem(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date); println(date.date.toString()) }
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
    // Animate the color transition based on the selection state for the container
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    )

    // Animate the shape's corner size based on selection state
    val shapeCornerSize by animateDpAsState(
        targetValue = if (isSelected) 40.dp else 15.dp
    )

    // Animate the text color change when the item is selected or not
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    )
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clickable { onClick() }
            .animateContentSize(), // Smooth animation for any content size changes
        shape = RoundedCornerShape(shapeCornerSize),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = if (isSelected) CardDefaults.elevatedCardElevation(4.dp) else CardDefaults.elevatedCardElevation(0.dp)
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
                style = MaterialTheme.typography.bodySmall.copy(color = textColor) // Apply text color here
            )
            Text(
                text = date.date.dayOfMonth.toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = if (isSelected) 23.sp else 16.sp,
                    color = textColor // Apply text color here
                ),
            )
        }
    }
}

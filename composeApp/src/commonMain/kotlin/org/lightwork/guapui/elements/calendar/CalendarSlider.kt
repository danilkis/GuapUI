package org.lightwork.guapui.elements.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.lightwork.guapui.viewmodel.CalendarViewModel

@Composable
fun CalendarSlider(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel
) {
    val dataSource = CalendarDataSource()
    var weekOffset by remember { mutableStateOf(0) }
    var selectedDate by remember { mutableStateOf<CalendarUiModel.Date?>(null) } // Track the selected date

    // Get the current date
    val currentDate = dataSource.today

    // Calculate the start date based on the week offset
    val startDate = currentDate.plus(DatePeriod(days = weekOffset * 7)) // Adjust by weeks (7 days per week)

    // Get CalendarUiModel based on the calculated start date
    val calendarUiModel = dataSource.getData(startDate = startDate, lastSelectedDate = currentDate)

    // Default the selected date to today
    if (selectedDate == null) {
        selectedDate = calendarUiModel.visibleDates.find { it.date == currentDate }
    }

    Column {
        Header(
            data = calendarUiModel,
            onPrevClick = { weekOffset -= 1 }, // Decrement week offset
            onNextClick = { weekOffset += 1 }  // Increment week offset
        )
        Content(
            data = calendarUiModel,
            selectedDate = selectedDate!!,
            onDateSelected = { selectedDate = it; viewModel.onDateSelected(it) }
        )
    }
}




@Composable
fun Header(
    data: CalendarUiModel,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row (modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (data.selectedDate.isToday) {
                "Today"
            } else {
                data.selectedDate.date.toString()
            },
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        IconButton(onClick = onPrevClick) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Back"
            )
        }
        IconButton(onClick = onNextClick) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "Next"
            )
        }
    }
}

@Composable
fun Content(
    data: CalendarUiModel,
    onDateSelected: (CalendarUiModel.Date) -> Unit,
    selectedDate: CalendarUiModel.Date
) {
    LazyRow (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        items(items = data.visibleDates) { date ->
            ContentItem(
                date = date,
                isSelected = date == selectedDate, // Compare with selectedDate
                onClick = { onDateSelected(date) } // Trigger when date is clicked
            )
        }
    }
}

@Composable
fun ContentItem(
    date: CalendarUiModel.Date,
    isSelected: Boolean, // Pass selection state here
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .width(60.dp)// Increase height to make selected card bigger
                .padding(8.dp) // Add padding for better spacing
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
                    fontSize = if (isSelected) 20.sp else 16.sp // Increase font size for selected date
                ),
            )
        }
    }
}



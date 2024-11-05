package org.lightwork.guapui.elements.calendar

import kotlinx.datetime.*
import kotlinx.datetime.LocalDate

data class CalendarUiModel(
    val selectedDate: Date, // The date selected by the User, default is today.
    val visibleDates: List<Date> // The dates shown on the screen.
) {

    val startDate: Date = visibleDates.first() // The first of the visible dates.
    val endDate: Date = visibleDates.last() // The last of the visible dates.

    data class Date(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean
    ) {
        // Replace `DateTimeFormatter.ofPattern("E")` with string formatting for day abbreviations
        val day: String = date.dayOfWeek.name.take(3).capitalize() // "Mon", "Tue", etc.
    }
}

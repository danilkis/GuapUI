package org.lightwork.guapui.elements.calendar

import kotlinx.datetime.DayOfWeek
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
        // Replace English day abbreviations with Russian ones
        val day: String = getDayAbbreviation(date.dayOfWeek)
    }
}

// Helper function to get Russian abbreviation of the day
fun getDayAbbreviation(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "ПН"
        DayOfWeek.TUESDAY -> "ВТ"
        DayOfWeek.WEDNESDAY -> "СР"
        DayOfWeek.THURSDAY -> "ЧТ"
        DayOfWeek.FRIDAY -> "ПТ"
        DayOfWeek.SATURDAY -> "СБ"
        DayOfWeek.SUNDAY -> "ВС"
        else -> TODO()
    }
}


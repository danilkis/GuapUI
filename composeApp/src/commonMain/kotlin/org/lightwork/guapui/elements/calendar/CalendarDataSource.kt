package org.lightwork.guapui.elements.calendar

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.Clock

class CalendarDataSource {

    val today: LocalDate
        get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    fun getData(startDate: LocalDate = today, lastSelectedDate: LocalDate, weekOffset: Int = 0): CalendarUiModel {
        // Calculate the starting date based on the week offset
        val adjustedStartDate = startDate.plus(DatePeriod(days = weekOffset * 7))
        val firstDayOfWeek = adjustedStartDate.withDayOfWeek(1) // First day of the week (Monday)
        val endDayOfWeek = firstDayOfWeek.plus(DatePeriod(days = 7)) // Add 7 days to get the end of the week
        val visibleDates = getDatesBetween(firstDayOfWeek, endDayOfWeek)
        return toUiModel(visibleDates, lastSelectedDate)
    }

    private fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val numOfDays = startDate.daysUntil(endDate)
        return (0 until numOfDays).map { startDate.plus(DatePeriod(days = it)) }
    }

    private fun toUiModel(
        dateList: List<LocalDate>,
        lastSelectedDate: LocalDate
    ): CalendarUiModel {
        return CalendarUiModel(
            selectedDate = toItemUiModel(lastSelectedDate, true),
            visibleDates = dateList.map {
                toItemUiModel(it, it == lastSelectedDate)
            }
        )
    }

    private fun toItemUiModel(date: LocalDate, isSelectedDate: Boolean) = CalendarUiModel.Date(
        isSelected = isSelectedDate,
        isToday = date == today,
        date = date,
    )

    private fun LocalDate.withDayOfWeek(dayOfWeek: Int): LocalDate {
        val currentDayOfWeek = this.dayOfWeek.isoDayNumber
        val diff = dayOfWeek - currentDayOfWeek
        return this.plus(DatePeriod(days = diff))
    }
}

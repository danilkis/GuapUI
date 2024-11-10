package org.lightwork.guapui.elements.calendar

import kotlinx.datetime.*

class CalendarDataSource {

    val today: LocalDate
        get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    fun getData(startDate: LocalDate = today, lastSelectedDate: LocalDate): CalendarUiModel {
        val endDate = startDate.plus(DatePeriod(months = 3)) // Cover 3 months
        val visibleDates = getDatesBetween(startDate, endDate)
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

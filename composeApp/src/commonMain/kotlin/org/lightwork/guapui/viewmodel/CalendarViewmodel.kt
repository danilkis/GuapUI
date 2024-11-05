package org.lightwork.guapui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.lightwork.guapui.elements.calendar.CalendarUiModel

class CalendarViewModel : ViewModel() {

    // Holds the current selected date
    private val _selectedDate = MutableStateFlow<LocalDate?>(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    // Function to update the selected date
    fun onDateSelected(date: CalendarUiModel.Date) {
        _selectedDate.value = date.date // Update the StateFlow with the new date
    }
}

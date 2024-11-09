package org.lightwork.guapui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import org.lightwork.guapui.functions.fetchGroups
import org.lightwork.guapui.functions.fetchLessons
import org.lightwork.guapui.functions.fetchWeekInfo
import org.lightwork.guapui.models.Day
import org.lightwork.guapui.models.Group
import org.lightwork.guapui.models.WeekInfo
import org.lightwork.guapui.providers.SettingsProvider

class ScheduleViewModel(private val settingsProvider: SettingsProvider) : ViewModel() {
    private val settings: Settings = settingsProvider.getSettings()

    private val _isOnboardingCompleted = MutableStateFlow(settings.getBoolean("onboarding_completed", false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted


    fun completeOnboarding() {
        settings["onboarding_completed"] = true
        _isOnboardingCompleted.value = true
    }

    private val _groups = MutableStateFlow<List<Group>?>(null)
    val groups: StateFlow<List<Group>?> = _groups

    private val _weekInfo = MutableStateFlow<WeekInfo?>(null)
    val weekInfo: StateFlow<WeekInfo?> = _weekInfo

    private val _lessons = MutableStateFlow<List<Day>?>(null)
    val lessons: StateFlow<List<Day>?> = _lessons

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var selectedGroupId: Int = settings.getInt("selected_group_id", 1032)
    var selectedDate: LocalDate? = null
    var selectedWeekType: String = "Авто"

    init {
        loadGroups()
        loadWeekInfo()
    }

    private fun loadGroups() {
        viewModelScope.launch {  // Use viewModelScope.launch instead of launch
            try {
                _groups.value = fetchGroups()
            } catch (e: Exception) {
                println("Error fetching groups: ${e.message}")
            }
        }
    }

    private fun loadWeekInfo() {
        viewModelScope.launch {  // Use viewModelScope.launch instead of launch
            try {
                _weekInfo.value = fetchWeekInfo()
                println("Week info fetched: ${_weekInfo.value}")
            } catch (e: Exception) {
                println("Error fetching week info: ${e.message}")
            }
        }
    }

    fun loadLessons() {
        if (selectedGroupId != 0 && _weekInfo.value != null) {
            _isLoading.value = true
            viewModelScope.launch {
                try {
                    // Определяем номер недели на основе типа недели
                    val weekNumber = when (selectedWeekType) {
                        "Числитель" -> 1
                        "Знаменатель" -> 2
                        "Авто" -> {
                            selectedDate?.let { date ->
                                if (date.weekOfYear() % 2 != 0) 2 else 1
                            } ?: 1 // по умолчанию 1, если selectedDate = null
                        }
                        else -> 1
                    }

                    _lessons.value = fetchLessons(selectedGroupId, weekNumber)
                    println("Lessons fetched for group $selectedGroupId and week $weekNumber")
                } catch (e: Exception) {
                    println("Error fetching lessons: ${e.message}")
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
    fun selectDate(date: LocalDate) {
        selectedDate = date
    }
    fun selectGroup(groupId: Int) {
        settings.putInt("selected_group_id", groupId)
        selectedGroupId = groupId
        loadLessons()
    }
}


fun LocalDate.weekOfYear(): Int {
    val startOfYear = LocalDate(this.year, 1, 1)
    val daysSinceStartOfYear = this.daysUntil(startOfYear)
    return (daysSinceStartOfYear / 7) + 1
}
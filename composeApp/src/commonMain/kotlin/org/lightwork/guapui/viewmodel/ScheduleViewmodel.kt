package org.lightwork.guapui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.lightwork.guapui.functions.fetchGroups
import org.lightwork.guapui.functions.fetchLessons
import org.lightwork.guapui.functions.fetchWeekInfo
import org.lightwork.guapui.models.Day
import org.lightwork.guapui.models.Group
import org.lightwork.guapui.models.WeekInfo
import org.lightwork.guapui.view.toInt

class ScheduleViewModel : ViewModel() {

    private val _groups = MutableStateFlow<List<Group>?>(null)
    val groups: StateFlow<List<Group>?> = _groups

    private val _weekInfo = MutableStateFlow<WeekInfo?>(null)
    val weekInfo: StateFlow<WeekInfo?> = _weekInfo

    private val _lessons = MutableStateFlow<List<Day>?>(null)
    val lessons: StateFlow<List<Day>?> = _lessons

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var selectedGroupId: Int = 0
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
            viewModelScope.launch {  // Use viewModelScope.launch instead of launch
                try {
                    val weekNumber = when (selectedWeekType) {
                        "Числитель" -> 1
                        "Знаменатель" -> 2
                        "Авто" -> _weekInfo.value!!.IsWeekOdd.toInt()
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

    fun selectGroup(groupId: Int) {
        selectedGroupId = groupId
        loadLessons()
    }

    fun selectWeekType(weekType: String) {
        selectedWeekType = weekType
        loadLessons()
    }
}
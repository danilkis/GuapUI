package org.lightwork.guapui.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiLesson(
    val ItemId: Int?,
    val Week: Int?,
    val Day: Int?,
    val Less: Int?,
    val Build: String?,
    val Rooms: String?,
    val Disc: String?,
    val Type: String?,
    val Groups: String?,
    val GroupsText: String?,
    val Preps: String?,
    val PrepsText: String?,
    val Dept: String?
)

@Serializable
data class Lesson(
    var breakTime: String = "10 минут",
    var showBreak: Boolean = true,
    var lessonName: String = "Математика",
    var teachers: List<String> = listOf("Иванов Иван Иванович", "Петров Петр Петрович"),
    var room: String = "101",
    var time: String = "10:10 - 11:40",
    var type: String = "Лекция",
    var number: Int = 1
)
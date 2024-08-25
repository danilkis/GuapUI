package org.lightwork.guapui.functions

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.lightwork.guapui.models.*

suspend fun fetchLessons(GroupId: Int, weekNumber: Int): List<Day> {
    val client = HttpClient {}

    // Map of day numbers to Russian day names
    val dayNames = mapOf(
        1 to "Понедельник",
        2 to "Вторник",
        3 to "Среда",
        4 to "Четверг",
        5 to "Пятница",
        6 to "Суббота",
        7 to "Воскресенье"
    )

    // Map of lesson numbers to their corresponding times
    val lessonTimes = mapOf(
        1 to "9:30 - 11:00",
        2 to "11:10 - 12:40",
        3 to "13:00 - 14:30",
        4 to "15:00 - 16:30",
        5 to "16:40 - 18:10",
        6 to "18:30 - 20:00"
    )

    try {
        // Get the response body as a String
        val response: String = client.get("https://api.guap.ru/rasp/custom/get-sem-rasp/group${GroupId}").bodyAsText()

        // Print the response for debugging purposes

        // Deserialize the response
        val apiLessons: Array<ApiLesson> = Json.decodeFromString(response)

        // Filter lessons based on the week number
        val filteredLessons = apiLessons.filter { it.Week?.toInt() == weekNumber }

        // Group lessons by Day, sort by day number, then by lesson number, and map to Day data class
        return filteredLessons.groupBy { it.Day ?: 0 }
            .entries // Convert to entries to use sortedMapOf
            .sortedBy { it.key } // Sort by day number
            .map { (dayNumber, lessons) ->
                Day(
                    dayName = dayNames[dayNumber] ?: "Неизвестный день", // Get the day name in Russian
                    lessons = lessons.sortedBy { it.Less?.toInt() ?: 0 }
                        .distinctBy { lesson ->
                            lesson.Disc.orEmpty() +
                            lesson.PrepsText.orEmpty() +
                            lesson.Rooms.orEmpty() +
                            (lesson.Less?.toInt() ?: 0) +
                            lesson.Build.orEmpty() +
                            lesson.Type.orEmpty()
                        }
                        .mapIndexed { index, apiLesson ->
                            val lessonNumber = apiLesson.Less?.toInt() ?: index + 1
                            Lesson(
                                lessonName = apiLesson.Disc.orEmpty(),
                                teachers = apiLesson.PrepsText?.split(";")?.map { it.trim() } ?: listOf("Нет информации"),
                                room = apiLesson.Rooms.orEmpty(),
                                time = lessonTimes[lessonNumber] ?: "Нет информации",
                                type = apiLesson.Type.orEmpty(),
                                number = lessonNumber,
                                building = apiLesson.Build ?: "Нет информации"
                            )
                        }
                )
            }
    } catch (e: Exception) {
        println("Error: ${e.message}")
        throw e
    } finally {
        client.close()
    }
}
suspend fun fetchGroups(): List<Group> {
    val client = HttpClient{}

    try {
        // Get the response body as a String
        val response: String = client.get("https://api.guap.ru/rasp/custom/get-sem-groups").bodyAsText()

        // Print the response for debugging purposes

        // Deserialize the response
        val groups: List<Group> = Json.decodeFromString(response)
        // Map ApiLesson to Lesson and return
        return groups
    } catch (e: Exception) {
        println("Error: ${e.message}")
        throw e
    } finally {
        client.close()
    }

}

suspend fun fetchWeekInfo(): WeekInfo? {
    val client = HttpClient{}
    return try {
        val response: String = client.get("https://api.guap.ru/rasp/custom/get-sem-info").bodyAsText()
        Json.decodeFromString(response)
    } catch (e: Exception) {
        throw e
    } finally {
        client.close()
    }
}
package org.lightwork.guapui.functions

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.datetime.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.lightwork.guapui.models.*
import kotlin.time.Duration



suspend fun fetchLessons(GroupId: Int, weekNumber: Int): List<Day> {
    val client = HttpClient {}
    val dayNames = mapOf(
        1 to "Понедельник",
        2 to "Вторник",
        3 to "Среда",
        4 to "Четверг",
        5 to "Пятница",
        6 to "Суббота",
        7 to "Воскресенье"
    )

    val lessonTimes = mapOf(
        1 to "9:30 - 11:00",
        2 to "11:10 - 12:40",
        3 to "13:00 - 14:30",
        4 to "15:00 - 16:30",
        5 to "16:40 - 18:10",
        6 to "18:30 - 20:00"
    )

    try {
        val response: String = client.get("https://api.guap.ru/rasp/custom/get-sem-rasp/group${GroupId}").bodyAsText()
        val apiLessons: Array<ApiLesson> = Json.decodeFromString(response)
        val filteredLessons = apiLessons.filter { it.Week?.toInt() == weekNumber }

        return filteredLessons.groupBy { it.Day ?: 0 }
            .entries
            .sortedBy { it.key }
            .map { (dayNumber, lessons) ->
                val sortedLessons = lessons.sortedBy { it.Less?.toInt() ?: 0 }
                val lessonList = mutableListOf<Lesson>()

                sortedLessons.forEachIndexed { index, apiLesson ->
                    val lessonNumber = apiLesson.Less?.toInt() ?: index + 1
                    val timeRange = lessonTimes[lessonNumber]?.split(" - ") ?: listOf("00:00", "00:00")
                    val startTime = timeRange[0]
                    val endTime = timeRange[1]

                    val (remainingTime, donePercentage) = if (Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.dayOfWeek.isoDayNumber == dayNumber &&
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time in LocalTime.parse(startTime)..LocalTime.parse(endTime)) {
                        calculateRemainingTime(startTime, endTime)
                    } else {
                        "" to 0
                    }

                    val lesson = Lesson(
                        lessonName = apiLesson.Disc.orEmpty(),
                        teachers = apiLesson.PrepsText?.split(";")?.map { it.trim() } ?: listOf("Нет информации"),
                        room = apiLesson.Rooms.orEmpty(),
                        time = lessonTimes[lessonNumber] ?: "Нет информации",
                        type = apiLesson.Type.orEmpty(),
                        number = lessonNumber,
                        building = apiLesson.Build ?: "Нет информации",
                        remainingTime = remainingTime,
                        donePercentage = donePercentage.takeIf { it > 0 }
                    )

                    // Calculate the break time for the current lesson
                    if (index < sortedLessons.size - 1) {
                        val nextLessonNumber = sortedLessons[index + 1].Less?.toInt() ?: lessonNumber + 1
                        val breakTime = calculateBreakTime(lessonTimes[lessonNumber], lessonTimes[nextLessonNumber])
                        if (breakTime != null) {
                            lesson.breakTime = breakTime
                            lesson.showBreak = true
                        }
                    }

                    lessonList.add(lesson)
                }

                Day(
                    dayName = dayNames[dayNumber] ?: "Неизвестный день",
                    lessons = lessonList
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
    val client = HttpClient {}
    return try {
        val response: String = client.get("https://api.guap.ru/rasp/custom/get-sem-groups").bodyAsText()
        Json.decodeFromString(response)
    } catch (e: Exception) {
        println("Error: ${e.message}")
        throw e
    }
}

suspend fun fetchWeekInfo(): WeekInfo? {
    val client = HttpClient {}
    return try {
        val response: String = client.get("https://api.guap.ru/rasp/custom/get-sem-info").bodyAsText()
        Json.decodeFromString(response)
    } catch (e: Exception) {
        println("Error: ${e.message}")
        throw e
    }
}

fun normalizeTimeString(time: String): String {
    // Split the time into hours and minutes
    val parts = time.split(":")
    val hours = parts[0].padStart(2, '0') // Ensure hours are 2 digits
    val minutes = parts[1].padStart(2, '0') // Ensure minutes are 2 digits, including the leading 0
    return "$hours:$minutes"
}
fun calculateBreakTime(currentLessonTime: String?, nextLessonTime: String?): String? {
    if (currentLessonTime == null || nextLessonTime == null) return null
    val currentEndTime = currentLessonTime.split(" - ").getOrNull(1) ?: return null
    val nextStartTime = nextLessonTime.split(" - ").getOrNull(0) ?: return null
    return "$currentEndTime - $nextStartTime"
}
fun calculateRemainingTime(startTime: String, endTime: String): Pair<String, Int> {
    val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    val lessonStartTime = LocalTime.parse(normalizeTimeString(startTime))
    val lessonEndTime = LocalTime.parse(normalizeTimeString(endTime))

    // Convert start, end, and current times to seconds from the start of the day
    val startSeconds = lessonStartTime.toSecondOfDay()
    val endSeconds = lessonEndTime.toSecondOfDay()
    val currentSeconds = currentTime.toSecondOfDay()

    // Calculate the total and remaining durations in seconds
    val totalDurationSeconds = endSeconds - startSeconds
    val remainingDurationSeconds = endSeconds - currentSeconds

    // Calculate the percentage of time completed
    val donePercentage = ((totalDurationSeconds - remainingDurationSeconds).toDouble() / totalDurationSeconds * 100).toInt()

    // Calculate hours and minutes from the remaining seconds
    val hours = remainingDurationSeconds / 3600
    val minutes = (remainingDurationSeconds % 3600) / 60

    // Determine the remaining time message
    val remainingTime = when {
        hours > 0 -> "Осталось $hours ${declineHour(hours.toLong())} $minutes ${declineMinute(minutes.toLong())}"
        minutes > 0 -> "Осталось $minutes ${declineMinute(minutes.toLong())}"
        else -> "Урок закончился"
    }

    return remainingTime to donePercentage
}


fun declineHour(hours: Long): String {
    return when {
        hours % 10 == 1L && hours % 100 != 11L -> "час"
        hours % 10 in 2L..4L && hours % 100 !in 12L..14L -> "часа"
        else -> "часов"
    }
}

fun declineMinute(minutes: Long): String {
    return when {
        minutes % 10 == 1L && minutes % 100 != 11L -> "минута"
        minutes % 10 in 2L..4L && minutes % 100 !in 12L..14L -> "минуты"
        else -> "минут"
    }
}

val buildingQRCodeMap = mapOf(
    "Гастелло 15" to "645e20bf4439659ffb28ff47",
    "Б.Морская 67" to "645e1f3d4439659ffb28bf8c",
    "Ленсовета 14" to "645e219a4439659ffb292374",
    "Московский 149" to "645e206d4439659ffb28f0e8"
)

val PartialJson = Json { ignoreUnknownKeys = true }

suspend fun fetchLessonBuildingNaviUrl(lesson: Lesson): String? {
    val client = HttpClient {}

    val buildingQRCodeId = buildingQRCodeMap[lesson.building]
    if (buildingQRCodeId.isNullOrEmpty()) {
        return null
    }

    return try {
        val qrCodeResponse = client.get("https://api.tango.vision/qrcodes/${buildingQRCodeId}").bodyAsText()
        val qrCode: TangoVisionQRCode = PartialJson.decodeFromString(qrCodeResponse)
        "https://qr.tango.vision/${qrCode.mall.settings.slug}/map?qrId=${qrCode.id}"
    } catch (e: Exception) {
        throw e
    } finally {
        client.close()
    }
}

suspend fun fetchLessonRoomNaviUrl(lesson: Lesson): String? {
    val client = HttpClient {}

    val buildingQRCodeId = buildingQRCodeMap[lesson.building]
    if (buildingQRCodeId.isNullOrEmpty()) {
        return null
    }

    return try {
        val qrCodeResponse = client.get("https://api.tango.vision/qrcodes/${buildingQRCodeId}").bodyAsText()
        val qrCode: TangoVisionQRCode = PartialJson.decodeFromString(qrCodeResponse)
        val shopsResponse = client.get("https://api.tango.vision/shop?mall=${qrCode.mall.id}").bodyAsText()
        val shops: List<TangoVisionShop> = PartialJson.decodeFromString(shopsResponse)

        val roomCode1 = lesson.room.replace("-", " ")
        val roomCode2 = lesson.room.replace("-", "")

        val shop = shops.find {
            it.roomNumber.contains(roomCode1) ||
            it.roomNumber.contains(roomCode2) ||
            it.searchTags.contains(roomCode1) ||
            it.searchTags.contains(roomCode2)
        }

        if(shop === null) null else "https://qr.tango.vision/${qrCode.mall.settings.slug}/map?qrId=${qrCode.id}&target=${shop.id}"
    } catch (e: Exception) {
        throw e
    } finally {
        client.close()
    }
}
package org.lightwork.guapui.functions

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.lightwork.guapui.models.ApiLesson
import org.lightwork.guapui.models.Lesson

suspend fun fetchLessons(): List<Lesson> {
    val client = HttpClient {}

    try {
        // Get the response body as a String
        val response: String = client.get("https://api.guap.ru/rasp/custom/get-sem-rasp/group89").bodyAsText()

        // Print the response for debugging purposes
        println("API Response: $response")

        // Deserialize the response
        val apiLessons: Array<ApiLesson> = Json.decodeFromString(response)
        println(apiLessons.toString())
        // Map ApiLesson to Lesson and return
        return apiLessons.map { apiLesson ->
            Lesson(
                lessonName = apiLesson.Disc.toString(),
                teachers = listOf(apiLesson.PrepsText!!),
                room = apiLesson.Rooms.toString(),
                time = "${apiLesson.Week}:${apiLesson.Day}:${apiLesson.Less}",
                type = apiLesson.Type.toString(),
                number = apiLesson.ItemId!!
            )
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
        throw e
    } finally {
        client.close()
    }
}
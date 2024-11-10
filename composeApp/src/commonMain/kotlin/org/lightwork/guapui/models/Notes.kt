package org.lightwork.guapui.models

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String,  // UUID должен быть строкой
    val created_at: LocalDateTime,
    val lessonNumber: Int,
    val date: LocalDate,
    val text: String,
    val user_uuid: String,
    val group: String,
    val title: String // Добавьте поле title, если оно нужно
)


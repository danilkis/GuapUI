package org.lightwork.guapui.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: Int?,
    val lessonNumber: Int,
    val date: LocalDate,
    val text: String,
    val user_uuid: String?,
    val group: String
)
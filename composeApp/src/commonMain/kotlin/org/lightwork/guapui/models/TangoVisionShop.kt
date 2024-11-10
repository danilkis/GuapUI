package org.lightwork.guapui.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TangoVisionShop(
    @SerialName("_id")
    val id: String,
    val index: Long? = null,
    val mall: String,
    val searchTags: String,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("__v")
    val v: Long,
    val roomNumber: String? = "",
)
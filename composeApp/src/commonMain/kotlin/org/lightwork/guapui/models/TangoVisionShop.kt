package org.lightwork.guapui.models

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class TangoVisionShop(
    @SerialName("_id")
    val id: String,
    val paymentType: JsonArray,
    val categories: List<String>,
    val agents: JsonArray,
    val index: Long? = null,
    val isActive: Boolean,
    val hideInSearch: Boolean,
    val gallery: JsonArray,
    val brandInfoAutoChange: Boolean,
//    val facade: Facade,
    val color: String,
    val schedule: JsonElement? = null,
    val group: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val website: JsonElement? = null,
    val address: JsonElement? = null,
    val renterPhone: JsonElement? = null,
    val renterName: JsonElement? = null,
    val mall: String,
    val showcase: JsonElement? = null,
    val searchTags: String,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("__v")
    val v: Long,
    val roomNumber: String,
    val logo: String? = null,
//    val social: Social? = null,
    val featuredIndex: JsonElement? = null
)
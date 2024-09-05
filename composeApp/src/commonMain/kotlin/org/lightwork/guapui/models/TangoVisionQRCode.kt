package org.lightwork.guapui.models

import kotlinx.serialization.*

// Really stripped since I don't care much about other fields
@Serializable
data class TangoVisionQRCode(
    @SerialName("_id")
    val id: String,
    val mall: TangoVisionQRCodeMall
)

@Serializable
data class TangoVisionQRCodeMall(
    @SerialName("_id")
    val id: String,
    val name: String,
    val settings: TangoVisionQRCodeMallSettings
)

@Serializable
data class TangoVisionQRCodeMallSettings(
    val slug: String
)
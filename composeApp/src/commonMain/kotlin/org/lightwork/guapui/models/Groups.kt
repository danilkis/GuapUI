package org.lightwork.guapui.models

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val Name: String,
    val ItemId: Int
)
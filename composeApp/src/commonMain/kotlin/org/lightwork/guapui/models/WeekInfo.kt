package org.lightwork.guapui.models
import kotlinx.serialization.Serializable

@Serializable
data class WeekInfo(
    val Years: String,
    val IsAutumn: Boolean,
    val Update: String,
    val CurrentWeek: Int,
    val IsWeekOdd: Boolean,
    val IsWeekUp: Boolean,
    val IsWeekRed: Boolean
)

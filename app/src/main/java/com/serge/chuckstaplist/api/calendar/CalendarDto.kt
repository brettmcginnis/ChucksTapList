package com.serge.chuckstaplist.api.calendar

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CalendarDto(
    val summary: String,
    val items: List<CalendarEntryDto>,
    val description: String? = null,
)

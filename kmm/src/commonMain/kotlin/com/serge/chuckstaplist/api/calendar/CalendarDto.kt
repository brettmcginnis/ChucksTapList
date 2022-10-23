package com.serge.chuckstaplist.api.calendar

import kotlinx.serialization.Serializable

@Serializable
data class CalendarDto(
    val summary: String,
    val items: List<CalendarEntryDto>,
    val description: String? = null,
)

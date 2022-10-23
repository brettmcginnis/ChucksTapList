package com.serge.chuckstaplist.api.calendar

import kotlinx.serialization.Serializable

@Serializable
data class CalendarEntryDto(
    val id: String,
    val status: String,
    val summary: String,
    val start: CalendarStartDateDto,
    val description: String? = null,
)

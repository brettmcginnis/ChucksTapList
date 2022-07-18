package com.serge.chuckstaplist.api.calendar

data class CalendarEntryDto(
    val id: String,
    val status: String,
    val summary: String,
    val description: String?,
    val start: CalendarStartDateDto
)

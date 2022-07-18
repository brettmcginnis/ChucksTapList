package com.serge.chuckstaplist.api.calendar

data class CalendarDto(
    val summary: String,
    val description: String,
    val items: List<CalendarEntryDto>
)

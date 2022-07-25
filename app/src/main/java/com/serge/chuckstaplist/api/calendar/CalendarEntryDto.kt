package com.serge.chuckstaplist.api.calendar

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CalendarEntryDto(
    val id: String,
    val status: String,
    val summary: String,
    val start: CalendarStartDateDto,
    val description: String? = null,
)

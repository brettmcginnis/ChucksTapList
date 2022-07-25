package com.serge.chuckstaplist.api.calendar

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CalendarStartDateDto(val dateTime: String)

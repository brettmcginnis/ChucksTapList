package com.serge.chuckstaplist.extensions

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime

fun Instant.toRFC3339(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val offset = timeZone.offsetAt(this).toString()
    return with(toLocalDateTime(timeZone)) { "$year-$monthNumber-${dayOfMonth}T$hour:$minute:$second$offset" }
}

fun LocalDateTime.toMonthDay(): String = "$monthNumber/$dayOfMonth"

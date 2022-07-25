package com.serge.chuckstaplist.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

const val RFC3339_FORMAT = "yyyy-MM-dd'T'h:m:ssZ"
const val MONTH_DAY_FORMAT = "MM/dd"

fun Date.addDays(days: Long): Date = Date(time + TimeUnit.DAYS.toMillis(days))
fun Date.subtractDays(days: Long): Date = Date(time - TimeUnit.DAYS.toMillis(days))

fun Date.toRFC3339(): String = SimpleDateFormat(RFC3339_FORMAT, Locale.getDefault()).format(this)
fun Date.toMonthDay(): String = SimpleDateFormat(MONTH_DAY_FORMAT, Locale.getDefault()).format(this)

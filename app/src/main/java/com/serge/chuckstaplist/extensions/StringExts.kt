package com.serge.chuckstaplist.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.RegexOption.IGNORE_CASE

fun String.toRFC3339Date(): Date? = runCatching {
    SimpleDateFormat(RFC3339_FORMAT, Locale.getDefault()).parse(this)
}.getOrNull()

fun String.removePrefix(prefixList: List<String>): String = prefixList.firstOrNull { prefix ->
    startsWith(prefix, ignoreCase = true)
}?.let { match ->
    split(match.toRegex(IGNORE_CASE), 2)[1]
} ?: this

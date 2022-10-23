package com.serge.chuckstaplist.extensions

import kotlin.text.RegexOption.IGNORE_CASE

fun String.removePrefix(prefixList: List<String>): String =
    prefixList.firstOrNull { prefix -> startsWith(prefix, ignoreCase = true) }
        ?.let { match -> split(match.toRegex(IGNORE_CASE), 2)[1] }
        ?: this

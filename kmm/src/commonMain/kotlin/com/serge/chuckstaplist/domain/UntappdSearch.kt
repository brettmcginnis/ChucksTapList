package com.serge.chuckstaplist.domain

import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.platform.ExternalBrowser
import com.serge.chuckstaplist.platform.encoded

fun ExternalBrowser.openUntappdSearch(tap: TapModel) {
    openUrl("https://untappd.com/search?q=${tap.extractSearchQuery()}")
}

@Suppress("RegExpRedundantEscape")
private val bracketRegex = "^.*\\{(.*)\\}$".toRegex()

private fun TapModel.extractSearchQuery(): String {
    val sanitizedName = (bracketRegex.find(name)?.groupValues?.get(1) ?: name)
    val breweryDividerIndex = sanitizedName.indexOf(':').takeIf { it >= 0 } ?: return sanitizedName
    val brewery = sanitizedName.substring(0, breweryDividerIndex).trim()
    val nameQuery = sanitizedName.substring(breweryDividerIndex, sanitizedName.length)
        .split(" ", "+", "-", ",", ":", ";")
        .filter { token -> token.any { it.isLetterOrDigit() } }
        .take(2)
        .joinToString(" ")
        .trim()

    return "$brewery $nameQuery".encoded()
}

package com.serge.chuckstaplist

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.serge.chuckstaplist.api.TapModel

@Suppress("RegExpRedundantEscape")
private val bracketRegex = "^.*\\{(.*)\\}$".toRegex()

fun Context.openUntappdSearch(tap: TapModel) {
    val uri = Uri.parse("https://untappd.com/search?q=${tap.extractSearchQuery()}")
    startActivity(this, Intent(Intent.ACTION_VIEW, uri), null)
}

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

    return Uri.encode("$brewery $nameQuery")
}

package com.serge.chuckstaplist.platform

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.ui.theme.DarkGray

actual class ExternalBrowser(private val activity: Activity) {
    
    actual fun openUrl(url: String) {
        CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(DarkGray.toArgb())
                    .build()
            )
            .build()
            .launchUrl(activity, url.toUri())
    }
    
    actual fun openUntappdSearch(tap: TapModel) {
        openUrl("https://untappd.com/search?q=${tap.extractSearchQuery()}")
    }
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

    return Uri.encode("$brewery $nameQuery")
}

package com.serge.chuckstaplist.platform

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
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
}

actual fun String.encoded(): String = Uri.encode(this)

package com.serge.chuckstaplist.chrometabs

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.toArgb
import com.serge.chuckstaplist.ui.theme.DarkGray

fun Context.showUrlInTab(url: String) =
    CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder().setToolbarColor(DarkGray.toArgb()).build())
        .build()
        .launchUrl(this, Uri.parse(url))

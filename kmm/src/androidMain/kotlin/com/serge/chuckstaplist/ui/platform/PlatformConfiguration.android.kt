package com.serge.chuckstaplist.ui.platform

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

actual val isLandscape: Boolean
    @Composable get() = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

actual val isIOS: Boolean = false

@Composable
actual fun getActivity(): Any? {
    val context = LocalContext.current
    return context as? Activity
}

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(enabled = enabled, onBack = onBack)
}

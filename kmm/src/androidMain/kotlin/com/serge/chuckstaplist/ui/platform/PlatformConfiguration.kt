package com.serge.chuckstaplist.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

actual val isLandscape: Boolean
    @Composable get() = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

actual val isIOS: Boolean = false
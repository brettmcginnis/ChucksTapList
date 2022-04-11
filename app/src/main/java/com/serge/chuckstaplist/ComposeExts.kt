package com.serge.chuckstaplist

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun isLandscape(): Boolean {
    val (screenWidth, screenHeight) = LocalConfiguration.current.run { screenWidthDp to screenHeightDp }
    return screenWidth > screenHeight
}

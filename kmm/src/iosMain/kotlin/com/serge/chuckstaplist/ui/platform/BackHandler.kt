package com.serge.chuckstaplist.ui.platform

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS handles back navigation differently - typically through navigation controller
    // This is a no-op for iOS as back navigation is handled by the platform
}
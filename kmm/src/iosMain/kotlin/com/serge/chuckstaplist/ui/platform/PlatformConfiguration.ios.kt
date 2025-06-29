package com.serge.chuckstaplist.ui.platform

import androidx.compose.runtime.Composable
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation

actual val isLandscape: Boolean
    @Composable get() {
        val orientation = UIDevice.currentDevice.orientation
        return orientation == UIDeviceOrientation.UIDeviceOrientationLandscapeLeft ||
                orientation == UIDeviceOrientation.UIDeviceOrientationLandscapeRight
    }

actual val isIOS: Boolean = true

// Android Specifics below which no-op on iOS
@Composable
actual fun getActivity(): Any? = null

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) = Unit

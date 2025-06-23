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
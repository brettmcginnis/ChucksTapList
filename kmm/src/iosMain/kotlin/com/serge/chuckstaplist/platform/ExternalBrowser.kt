package com.serge.chuckstaplist.platform

import com.serge.chuckstaplist.api.TapModel

actual class ExternalBrowser {
    actual fun openUrl(url: String) {
        // No-op for iOS - can be implemented later with platform.Foundation.NSURL
    }
    
    actual fun openUntappdSearch(tap: TapModel) {
        // No-op for iOS
    }
}
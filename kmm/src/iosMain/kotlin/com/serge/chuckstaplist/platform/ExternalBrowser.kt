package com.serge.chuckstaplist.platform

import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.UIKit.UIApplication

actual class ExternalBrowser {
    actual fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url)
        nsUrl?.let { 
            UIApplication.sharedApplication.openURL(it, emptyMap<Any?, Any?>()) {}
        }
    }
}

@Suppress("CAST_NEVER_SUCCEEDS")
actual fun String.encoded() : String =
    (this as NSString)
        .stringByAddingPercentEncodingWithAllowedCharacters(NSCharacterSet.URLQueryAllowedCharacterSet)
        ?: replace(' ', '+')

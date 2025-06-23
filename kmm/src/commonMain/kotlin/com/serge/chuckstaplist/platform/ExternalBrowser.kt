package com.serge.chuckstaplist.platform

import com.serge.chuckstaplist.api.TapModel

expect class ExternalBrowser {
    fun openUrl(url: String)
    fun openUntappdSearch(tap: TapModel)
}
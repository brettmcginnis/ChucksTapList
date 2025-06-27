package com.serge.chuckstaplist.platform

expect class ExternalBrowser {
    fun openUrl(url: String)
}

expect fun String.encoded() : String

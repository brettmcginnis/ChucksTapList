package com.serge.chuckstaplist.platform

import org.koin.dsl.module

actual val chucksPlatformModule = module {
    factory { ShakeDetector() }
    factory { ExternalBrowser() }
}

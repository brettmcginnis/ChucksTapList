package com.serge.chuckstaplist.platform

import org.koin.dsl.module

actual val platformModule = module {
    factory { ShakeDetector() }
    factory { ExternalBrowser() }
}
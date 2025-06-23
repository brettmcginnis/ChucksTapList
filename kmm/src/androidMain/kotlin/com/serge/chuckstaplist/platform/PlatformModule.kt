package com.serge.chuckstaplist.platform

import android.app.Activity
import org.koin.dsl.module

actual val chucksPlatformModule = module {
    factory { ShakeDetector(get()) }
    factory { (activity: Activity) -> ExternalBrowser(activity) }
}

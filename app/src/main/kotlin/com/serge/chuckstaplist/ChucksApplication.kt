package com.serge.chuckstaplist

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ChucksApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) androidLogger()
            androidContext(this@ChucksApplication)
            modules(module { single { koin.logger } }, chucksModule(BuildConfig.CALENDAR_API_KEY))
        }
    }
}

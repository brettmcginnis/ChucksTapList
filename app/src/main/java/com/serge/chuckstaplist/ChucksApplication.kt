package com.serge.chuckstaplist

import android.app.Application
import com.serge.chuckstaplist.api.chucksApiModule
import com.serge.chuckstaplist.foodtruck.FoodTruckModule
import com.serge.chuckstaplist.network.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

class ChucksApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ChucksApplication)
            modules(networkModule, chucksApiModule, defaultModule, FoodTruckModule().module)
        }
    }
}

package com.serge.chuckstaplist

import com.serge.chuckstaplist.api.chucksApiModule
import com.serge.chuckstaplist.domain.domainModule
import com.serge.chuckstaplist.foodtruck.FoodTruckRepository
import com.serge.chuckstaplist.platform.platformModule
import org.koin.dsl.module

fun chucksModule(calendarApiKey: String) = module {
    includes(chucksApiModule(calendarApiKey))
    includes(domainModule)
    includes(platformModule)
    factory { FoodTruckRepository(get(), get(), logger) }
}

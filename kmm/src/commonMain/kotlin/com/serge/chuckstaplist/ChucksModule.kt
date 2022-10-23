package com.serge.chuckstaplist

import com.serge.chuckstaplist.api.chucksApiModule
import com.serge.chuckstaplist.foodtruck.FoodTruckRepository
import com.serge.chuckstaplist.foodtruck.FoodTruckViewModel
import org.koin.dsl.module

fun chucksModule(calendarApiKey: String) = module {
    includes(chucksApiModule(calendarApiKey))
    factory { FoodTruckRepository(get(), get(), get()) }
    vm { FoodTruckViewModel(get()) }
    vm { TapListViewModel(get()) }
}

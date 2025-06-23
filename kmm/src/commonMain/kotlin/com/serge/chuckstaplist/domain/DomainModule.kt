package com.serge.chuckstaplist.domain

import com.serge.chuckstaplist.api.chucksApiModule
import com.serge.chuckstaplist.domain.usecases.GetFoodTrucksUseCase
import com.serge.chuckstaplist.domain.usecases.GetTapListUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal fun chucksDomainModule(calendarApiKey: String) = module {
    includes(chucksApiModule(calendarApiKey))
    factoryOf(::GetTapListUseCase)
    factoryOf(::GetFoodTrucksUseCase)
    factory { FoodTruckRepository(get(), get(), logger) }
}

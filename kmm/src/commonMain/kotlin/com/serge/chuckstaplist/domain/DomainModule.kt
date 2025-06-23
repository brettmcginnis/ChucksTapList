package com.serge.chuckstaplist.domain

import com.serge.chuckstaplist.domain.usecases.GetFoodTrucksUseCase
import com.serge.chuckstaplist.domain.usecases.GetTapListUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetTapListUseCase)
    factoryOf(::GetFoodTrucksUseCase)
}
package com.serge.chuckstaplist

import com.serge.chuckstaplist.domain.chucksDomainModule
import com.serge.chuckstaplist.platform.chucksPlatformModule
import org.koin.dsl.module

fun chucksModule(calendarApiKey: String) = module {
    includes(chucksPlatformModule)
    includes(chucksDomainModule(calendarApiKey))
}

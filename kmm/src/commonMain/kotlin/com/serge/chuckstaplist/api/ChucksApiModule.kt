package com.serge.chuckstaplist.api

import com.serge.chuckstaplist.api.calendar.CalendarApiKey
import com.serge.chuckstaplist.network.networkModule
import org.koin.dsl.module

fun chucksApiModule(calendarApiKey: String) = module {
    includes(networkModule)
    single { CalendarApiKey(calendarApiKey) }
    factory { ChucksApi(get()) }
    factory { GoogleCalendarApi(get()) }
}

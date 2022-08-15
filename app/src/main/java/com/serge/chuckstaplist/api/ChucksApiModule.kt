package com.serge.chuckstaplist.api

import com.serge.chuckstaplist.BuildConfig
import com.serge.chuckstaplist.api.calendar.CalendarApiKey
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
class ChucksApiModule {
    @Single
    fun calendarApiKey() = CalendarApiKey(BuildConfig.CALENDAR_API_KEY)
}

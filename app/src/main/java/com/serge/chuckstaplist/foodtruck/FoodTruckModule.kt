package com.serge.chuckstaplist.foodtruck

import com.serge.chuckstaplist.BuildConfig
import com.serge.chuckstaplist.api.calendar.CalendarApiKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FoodTruckModule {

    @Provides
    fun providesCalendarApiKey() = CalendarApiKey(BuildConfig.CALENDAR_API_KEY)
}

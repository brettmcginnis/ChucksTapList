package com.serge.chuckstaplist.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.serge.chuckstaplist.BuildConfig
import com.serge.chuckstaplist.api.calendar.CalendarApiKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import org.koin.dsl.module
import retrofit2.Retrofit

@OptIn(ExperimentalSerializationApi::class)
val chucksApiModule = module {
    single { get<Retrofit>().create(ChucksApi::class.java) }
    single { CalendarApiKey(BuildConfig.CALENDAR_API_KEY) }
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl("https://www.googleapis.com/calendar/v3/calendars/")
            .addConverterFactory(get<Json>().asConverterFactory(MediaType.get("application/json")))
            .build()
            .create(GoogleCalendarApi::class.java)
    }
}

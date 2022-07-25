package com.serge.chuckstaplist.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(ActivityRetainedComponent::class)
object ChucksApiModule {

    @Provides
    fun provideChucksApi(retrofit: Retrofit): ChucksApi = retrofit.create(ChucksApi::class.java)

    @Provides
    fun provideGoogleCalendarApi(okHttpClient: OkHttpClient, json: Json): GoogleCalendarApi =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://www.googleapis.com/calendar/v3/calendars/")
            .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
            .build()
            .create(GoogleCalendarApi::class.java)
}

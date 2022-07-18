package com.serge.chuckstaplist.api

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ActivityRetainedComponent::class)
object ChucksApiModule {

    @Provides
    fun provideChucksApi(retrofit: Retrofit): ChucksApi = retrofit.create(ChucksApi::class.java)

    @Provides
    fun provideGoogleCalendarApi(okHttpClient: OkHttpClient, gson: Gson): GoogleCalendarApi =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://www.googleapis.com/calendar/v3/calendars/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GoogleCalendarApi::class.java)
}

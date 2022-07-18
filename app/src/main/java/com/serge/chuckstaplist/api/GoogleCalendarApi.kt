package com.serge.chuckstaplist.api

import com.serge.chuckstaplist.api.calendar.CalendarDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleCalendarApi {
    @GET("{calendarId}/events")
    suspend fun getCalendar(
        @Path("calendarId") calendarId: String,
        @Query("key") apiKey: String,
        @Query("timeMin") timeMin: String,
        @Query("timeMax") timeMax: String
    ): CalendarDto
}
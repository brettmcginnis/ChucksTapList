package com.serge.chuckstaplist.api

import com.serge.chuckstaplist.api.calendar.CalendarDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import org.koin.core.annotation.Factory

private const val BASE_URL = "https://www.googleapis.com/calendar/v3/calendars"

@Factory
class GoogleCalendarApi(private val client: HttpClient) {
    suspend fun getCalendar(calendarId: String, apiKey: String, timeMin: String, timeMax: String): CalendarDto =
        client.post("$BASE_URL/$calendarId/events?singleEvents=true&key=$apiKey&timeMin=$timeMin&timeMax=$timeMax").body()
}

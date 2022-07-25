package com.serge.chuckstaplist.foodtruck

import android.util.Log
import androidx.core.text.HtmlCompat
import com.serge.chuckstaplist.api.GoogleCalendarApi
import com.serge.chuckstaplist.api.calendar.CalendarApiKey
import com.serge.chuckstaplist.api.calendar.CalendarEntryDto
import com.serge.chuckstaplist.extensions.addDays
import com.serge.chuckstaplist.extensions.removePrefix
import com.serge.chuckstaplist.extensions.subtractDays
import com.serge.chuckstaplist.extensions.toMonthDay
import com.serge.chuckstaplist.extensions.toRFC3339
import com.serge.chuckstaplist.extensions.toRFC3339Date
import java.util.Date
import javax.inject.Inject

private const val FOOD_TRUCK_DAYS = 7L
private const val STATUS_CONFIRMED = "confirmed"
private const val TAG = "CalendarHelper"

class FoodTruckRepository @Inject constructor(
    private val googleCalendarApi: GoogleCalendarApi,
    private val calendarApiKey: CalendarApiKey,
) {
    suspend fun getFoodTrucks(calendarId: String, days: Long = FOOD_TRUCK_DAYS): List<FoodTruckEvent> = runCatching {
        val timeMin = Date().toRFC3339()
        val timeMax = Date().addDays(days).toRFC3339()
        return googleCalendarApi.getCalendar(calendarId, calendarApiKey.key, timeMin, timeMax).items.toTruckEventModel()
    }.onFailure {
        Log.e(TAG, "issue parsing calendar $calendarId", it)
    }.getOrElse { emptyList() }

    private fun List<CalendarEntryDto>.toTruckEventModel(): List<FoodTruckEvent> {
        val yesterday = Date().subtractDays(1)
        return filter { it.status == STATUS_CONFIRMED }
            .filter { it.start.dateTime.toRFC3339Date()?.after(yesterday) == true }
            .sortedBy { it.start.dateTime.toRFC3339Date() }
            .fold(mutableListOf()) { acc, event ->
                event.start.dateTime.toRFC3339Date()?.let { eventDate ->
                    val sanitizedName = event.summary.removePrefix(namePrefixes)
                    acc.add(
                        FoodTruckEvent(
                            name = sanitizedName.addDatePrefix(eventDate),
                            url = event.description.asUrlOrWithGoogleSearchTerm(sanitizedName),
                            date = eventDate
                        )
                    )
                }
                acc
            }
    }

    private fun String.addDatePrefix(date: Date) = "${date.toMonthDay()} - $this"

    private fun String?.asUrlOrWithGoogleSearchTerm(searchTerm: String): String = runCatching {
        if (this == null) return searchTerm.asGoogleSearchUrl()
        HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }.getOrElse { searchTerm.asGoogleSearchUrl() }

    private fun String.asGoogleSearchUrl() =
        "https://www.google.com/search?q=food+truck+seattle+${this.replace(" ", "+")}"

    /**
     * Useless prefixes that come across in the calendar event name that we can take away
     */
    private val namePrefixes = listOf(
        "dinner: ",
        "brunch: ",
        "lunch:",
        "food truck: ",
        "pop up- ",
        "pop up- ",
        "pop up - ",
    )
}

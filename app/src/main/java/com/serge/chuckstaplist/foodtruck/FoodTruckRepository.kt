package com.serge.chuckstaplist.foodtruck

import android.util.Log
import androidx.core.text.HtmlCompat
import com.serge.chuckstaplist.api.GoogleCalendarApi
import com.serge.chuckstaplist.api.calendar.CalendarApiKey
import com.serge.chuckstaplist.api.calendar.CalendarEntryDto
import com.serge.chuckstaplist.extensions.removePrefix
import com.serge.chuckstaplist.extensions.toMonthDay
import com.serge.chuckstaplist.extensions.toRFC3339
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.days

private const val FOOD_TRUCK_DAYS = 7L
private const val STATUS_CONFIRMED = "confirmed"
private const val TAG = "CalendarHelper"

@Factory
class FoodTruckRepository(
    private val googleCalendarApi: GoogleCalendarApi,
    private val calendarApiKey: CalendarApiKey,
) {
    suspend fun getFoodTrucks(calendarId: String, days: Long = FOOD_TRUCK_DAYS): List<FoodTruckEvent> = runCatching {
        val now = Clock.System.now()
        val timeMin = Clock.System.now().toRFC3339()
        val timeMax = now.plus(days.days).toRFC3339()
        return googleCalendarApi.getCalendar(calendarId, calendarApiKey.key, timeMin, timeMax).items.toTruckEventModel()
    }.onFailure {
        Log.e(TAG, "issue parsing calendar $calendarId", it)
    }.getOrElse { emptyList() }

    private fun List<CalendarEntryDto>.toTruckEventModel(): List<FoodTruckEvent> {
        val now = Clock.System.now()
        val yesterday = now.minus(1.days)
        return asSequence()
            .filter { it.status == STATUS_CONFIRMED }
            .map { entry -> entry to entry.dateTime }
            .filter { (_, time) -> time > yesterday }
            .sortedBy { (_, time) -> time }
            .fold(mutableListOf()) { acc, (event, time) ->
                val sanitizedName = event.summary.removePrefix(namePrefixes)
                val eventDate = time.toLocalDateTime(TimeZone.currentSystemDefault())
                acc.apply {
                    add(
                        FoodTruckEvent(
                            name = sanitizedName.addDatePrefix(eventDate),
                            url = event.description.asUrlOrWithGoogleSearchTerm(sanitizedName),
                            date = eventDate
                        )
                    )
                }
            }
    }

    private fun String.addDatePrefix(date: LocalDateTime) = "${date.toMonthDay()} - $this"

    private fun String?.asUrlOrWithGoogleSearchTerm(searchTerm: String): String = runCatching {
        if (this == null) return searchTerm.asGoogleSearchUrl()
        HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }.getOrElse { searchTerm.asGoogleSearchUrl() }

    @Suppress("MagicNumber")
    private val CalendarEntryDto.dateTime get() = with(start.dateTime) {
        val isoString = dropLast(6)
        val offsetString = takeLast(6)
        LocalDateTime.parse(isoString).toInstant(UtcOffset.parse(offsetString))
    }

    private fun String.asGoogleSearchUrl() =
        "https://www.google.com/search?q=food+truck+seattle+${replace(" ", "+")}"

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

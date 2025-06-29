package com.serge.chuckstaplist.ui.extensions

import androidx.compose.ui.graphics.Color.Companion.White
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.api.markupPerOz
import com.serge.chuckstaplist.api.markupPerPour
import com.serge.chuckstaplist.api.price
import com.serge.chuckstaplist.api.serving
import com.serge.chuckstaplist.api.showCrowler
import com.serge.chuckstaplist.api.showGrowler
import com.serge.chuckstaplist.platform.encoded
import com.serge.chuckstaplist.ui.theme.Green
import com.serge.chuckstaplist.ui.theme.Orange
import com.serge.chuckstaplist.ui.theme.Pink
import com.serge.chuckstaplist.ui.theme.Red
import com.serge.chuckstaplist.ui.theme.Sky
import com.serge.chuckstaplist.ui.theme.Yellow

val TapModel.colorValue get() = when (color) {
    "orange" -> Orange
    "yellow" -> Yellow
    "green" -> Green
    "pink" -> Pink
    "sky" -> Sky
    "red" -> Red
    else -> White
}

private const val UNKNOWN_VALUE = "???"

data class TapInfoText(val text: String, val numLines: Int = 1)

val TapModel.info get() = listOf(
    TapInfoText(tapNumber.toString()),
    TapInfoText(name, numLines = 3),
    TapInfoText(price?.let { "$$it" } ?: UNKNOWN_VALUE),
    TapInfoText(origin ?: UNKNOWN_VALUE),
    TapInfoText("${abv ?: 0}%")
)

val TapModel.servingSizeFormatted
    get() = when (serving) {
        0 -> "???"
        else -> "$serving oz"
    }

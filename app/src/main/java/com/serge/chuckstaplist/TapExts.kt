package com.serge.chuckstaplist

import androidx.compose.ui.graphics.Color.Companion.White
import com.serge.chuckstaplist.api.TapModel
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

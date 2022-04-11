package com.serge.chuckstaplist.ui.theme

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

val DarkGreen = Color(17, 166, 24)
val Orange = Color(255, 170, 0)
val Yellow = Color(247, 255, 0)
val Green = Color(43, 255, 28)
val Pink = Color(255, 0, 247)
val Sky = Color(38, 241, 255)
val Red = Color(255, 0, 0)
val DarkGray = Color(32, 32, 32)
val LightGray = Color(200, 200, 200)

val colorSetStateSaver = Saver<MutableState<Set<Color>>, Set<Int>>(
    save = { state -> state.value.mapTo(HashSet(), Color::toArgb) },
    restore = { colors -> mutableStateOf(colors.mapTo(HashSet(), ::Color)) },
)

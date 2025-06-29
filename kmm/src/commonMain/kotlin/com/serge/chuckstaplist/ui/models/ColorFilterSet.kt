package com.serge.chuckstaplist.ui.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

@Immutable
class ColorFilterSet(set: Set<Color> = emptySet()) : Set<Color> by set {
    companion object {
        val Saver = Saver<MutableState<ColorFilterSet>, Set<Int>>(
            save = { state -> state.value.mapTo(HashSet(), Color::toArgb) },
            restore = { colors -> mutableStateOf(ColorFilterSet(colors.mapTo(HashSet(), ::Color))) },
        )
    }
}
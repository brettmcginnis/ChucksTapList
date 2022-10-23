package com.serge.chuckstaplist

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

@Composable
fun ColorFilterHeader(
    colorFilterSet: ColorFilterSet,
    onFilterStateUpdated: (ColorFilterSet) -> Unit,
) = Column {
    val colorFilterSetState by rememberUpdatedState(colorFilterSet)
    val onFilterStateUpdatedState by rememberUpdatedState(onFilterStateUpdated)

    fun updateColorFilterState(selectedColor: Color) =
        with(colorFilterSetState) { if (contains(selectedColor)) minus(selectedColor) else plus(selectedColor) }
            .run(::ColorFilterSet)
            .run(onFilterStateUpdatedState)

    ColorFilterRow(colorFilterSetState, ::updateColorFilterState)
}

@Immutable
class ColorFilterSet(set: Set<Color> = emptySet()) : Set<Color> by set {
    companion object {
        val Saver = Saver<MutableState<ColorFilterSet>, Set<Int>>(
            save = { state -> state.value.mapTo(HashSet(), Color::toArgb) },
            restore = { colors -> mutableStateOf(ColorFilterSet(colors.mapTo(HashSet(), ::Color))) },
        )
    }
}

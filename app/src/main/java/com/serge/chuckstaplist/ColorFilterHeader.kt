package com.serge.chuckstaplist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun ColorFilterHeader(
    colorFilterSet: ColorFilterSet,
    onFilterStateUpdated: (ColorFilterSet) -> Unit,
    content: @Composable () -> Unit
) = Column {
    val colorFilterSetState by rememberUpdatedState(colorFilterSet)
    val onFilterStateUpdatedState by rememberUpdatedState(onFilterStateUpdated)

    fun updateColorFilterState(selectedColor: Color) =
        with(colorFilterSetState) { if (contains(selectedColor)) minus(selectedColor) else plus(selectedColor) }
            .run(::ColorFilterSet)
            .run(onFilterStateUpdatedState)

    if (LocalConfiguration.current.isLandscape) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceAround, Alignment.CenterVertically) {
            content()
            ColorFilterRow(colorFilterSetState, ::updateColorFilterState)
        }
    } else {
        Box(Modifier.fillMaxWidth(), Alignment.Center) { content() }

        Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
            ColorFilterRow(colorFilterSetState, ::updateColorFilterState)
        }
    }
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

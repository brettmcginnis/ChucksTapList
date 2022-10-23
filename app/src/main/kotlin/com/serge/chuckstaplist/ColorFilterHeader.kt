package com.serge.chuckstaplist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.dp

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

    Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
        Text("Filter:", modifier = Modifier.padding(horizontal = 4.dp), color = Color.White,)
        ColorFilterRow(colorFilterSetState, ::updateColorFilterState)
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

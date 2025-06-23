package com.serge.chuckstaplist.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import com.serge.chuckstaplist.ui.models.ColorFilterSet

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
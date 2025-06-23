package com.serge.chuckstaplist.ui.circuit

import androidx.compose.runtime.Immutable

@Immutable
data class TapHighlightState(
    val highlightedTapIndex: Int = -1,
    val currentColorIndex: Int = 0,
    val shouldScrollToHighlighted: Boolean = false
)

package com.serge.chuckstaplist

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver

@Immutable
class ExpandedTaps(expandedTaps: Set<Int> = emptySet()) : Set<Int> by expandedTaps {
    companion object {
        val Saver = Saver<MutableState<ExpandedTaps>, Set<Int>>(
            save = { state -> state.value.toSet() },
            restore = { taps -> mutableStateOf(ExpandedTaps(taps)) },
        )
    }
}

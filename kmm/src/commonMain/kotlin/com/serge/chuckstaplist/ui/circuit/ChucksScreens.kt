package com.serge.chuckstaplist.ui.circuit

import com.serge.chuckstaplist.ChucksStore
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data object StoreSelectionScreen : Screen

@Parcelize
data class TapListScreen(val store: ChucksStore) : Screen

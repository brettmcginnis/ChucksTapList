package com.serge.chuckstaplist.ui.circuit

import com.serge.chuckstaplist.ChucksStore
import com.slack.circuit.runtime.screen.Screen

actual data object StoreSelectionScreen : Screen

actual data class TapListScreen actual constructor(actual val store: ChucksStore) : Screen

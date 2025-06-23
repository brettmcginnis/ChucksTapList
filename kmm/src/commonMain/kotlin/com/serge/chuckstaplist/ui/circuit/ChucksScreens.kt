package com.serge.chuckstaplist.ui.circuit

import com.serge.chuckstaplist.ChucksStore
import com.slack.circuit.runtime.screen.Screen

expect object StoreSelectionScreen : Screen

expect class TapListScreen(store: ChucksStore) : Screen {
    val store : ChucksStore
}

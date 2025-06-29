package com.serge.chuckstaplist.ui.circuit

import android.os.Parcelable
import com.serge.chuckstaplist.ChucksStore
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
actual data object StoreSelectionScreen : Screen, Parcelable

@Parcelize
actual data class TapListScreen actual constructor(actual val store: ChucksStore) : Screen, Parcelable

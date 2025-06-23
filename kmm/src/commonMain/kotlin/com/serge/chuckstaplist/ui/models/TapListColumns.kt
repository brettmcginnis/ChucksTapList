package com.serge.chuckstaplist.ui.models

import androidx.compose.runtime.Immutable
import com.serge.chuckstaplist.domain.FoodTruckEvent

@Immutable
class TapListColumns(columns: List<TapListColumn>) : List<TapListColumn> by columns {
    @Immutable 
    class Weights(weights: List<Float>) : List<Float> by weights

    val weights by lazy { Weights(map { it.weight }) }
}

@Immutable
class TapList(taps: List<com.serge.chuckstaplist.api.TapModel>) : List<com.serge.chuckstaplist.api.TapModel> by taps

@Immutable
class FoodTruckList(foodTrucks: List<FoodTruckEvent>) : List<FoodTruckEvent> by foodTrucks

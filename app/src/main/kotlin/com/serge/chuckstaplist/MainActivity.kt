package com.serge.chuckstaplist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.serge.chuckstaplist.chrometabs.showUrlInTab
import com.serge.chuckstaplist.foodtruck.FoodTruckEvent
import com.serge.chuckstaplist.foodtruck.FoodTruckViewModel
import com.serge.chuckstaplist.ui.theme.ChucksTapListTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val onTruckEventSelected: (FoodTruckEvent) -> Unit = { truckEvent -> showUrlInTab(truckEvent.url) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChucksTapListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val tapListViewModel by viewModel<TapListViewModel>()
                    val foodTruckViewModel by viewModel<FoodTruckViewModel>()

                    var selectedStore by rememberSaveable { mutableStateOf(ChucksStore.GREENWOOD) }

                    val tapListState by tapListViewModel.state.collectAsState()
                    val taps = (tapListState as? TapListViewModel.State.StoreInfo)
                        ?.taps.orEmpty().run(::TapList)

                    val foodTruckState by foodTruckViewModel.state.collectAsState()
                    val foodTrucks = (foodTruckState as? FoodTruckViewModel.State.TruckList)
                        ?.foodTrucks.orEmpty().run(::FoodTruckList)

                    SideEffect {
                        if (tapListState is TapListViewModel.State.Empty) tapListViewModel.loadTapList(selectedStore)
                        if (foodTruckState is FoodTruckViewModel.State.Empty) foodTruckViewModel.loadFoodTrucks(selectedStore)
                    }

                    ListChucksTaps(taps, foodTrucks, selectedStore, tapListState is TapListViewModel.State.Loading, onTruckEventSelected) {
                        selectedStore = it.also(tapListViewModel::loadTapList).also(foodTruckViewModel::loadFoodTrucks)
                    }
                }
            }
        }
    }
}

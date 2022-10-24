package com.serge.chuckstaplist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val tapListViewModel by viewModel<TapListViewModel>()
                    val foodTruckViewModel by viewModel<FoodTruckViewModel>()

                    var selectedStore by rememberSaveable { mutableStateOf<ChucksStore?>(null) }
                    val isStoreSelected = selectedStore != null

                    val tapListState by tapListViewModel.state.collectAsState()
                    val taps = (tapListState as? TapListViewModel.State.StoreInfo)?.taps.orEmpty().run(::TapList)
                    val foodTruckState by foodTruckViewModel.state.collectAsState()
                    val foodTrucks = (foodTruckState as? FoodTruckViewModel.State.TruckList)?.foodTrucks.orEmpty().run(::FoodTruckList)

                    AnimatedVisibility(
                        visible = !isStoreSelected,
                        enter = slideInHorizontally { -it },
                        exit = slideOutHorizontally { -it }
                    ) { StoreSelector(onStoreSelected = { selectedStore = it }) }

                    AnimatedVisibility(
                        visible = isStoreSelected,
                        enter = slideInHorizontally { it },
                        exit = slideOutHorizontally { it }
                    ) {
                        val store = selectedStore ?: return@AnimatedVisibility
                        ListChucksTaps(taps, foodTrucks, store, tapListState is TapListViewModel.State.Loading, onTruckEventSelected) {
                            tapListViewModel.loadTapList(store, force = true)
                            foodTruckViewModel.loadFoodTrucks(store, force = true)
                        }
                    }

                    LaunchedEffect(selectedStore) {
                        selectedStore?.run(tapListViewModel::loadTapList)
                        selectedStore?.run(foodTruckViewModel::loadFoodTrucks)
                    }

                    BackHandler(selectedStore != null) { selectedStore = null }
                }
            }
        }
    }
}

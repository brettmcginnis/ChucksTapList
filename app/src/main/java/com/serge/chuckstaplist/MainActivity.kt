package com.serge.chuckstaplist

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewmodel.compose.viewModel
import com.serge.chuckstaplist.TapListViewModel.State.Empty
import com.serge.chuckstaplist.TapListViewModel.State.Loading
import com.serge.chuckstaplist.TapListViewModel.State.StoreInfo
import com.serge.chuckstaplist.foodtruck.FoodTruckEvent
import com.serge.chuckstaplist.ui.theme.ChucksTapListTheme
import com.serge.chuckstaplist.ui.theme.DarkGray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val onTruckEventSelected: (FoodTruckEvent) -> Unit = { truckEvent ->
        CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder().setToolbarColor(DarkGray.toArgb()).build())
            .build().launchUrl(this, Uri.parse(truckEvent.url))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChucksTapListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel: TapListViewModel = viewModel()

                    var selectedStore by rememberSaveable { mutableStateOf(ChucksStore.GREENWOOD) }
                    val state by viewModel.state.collectAsState()
                    val taps = (state as? StoreInfo)?.taps.orEmpty().run(::TapList)
                    val foodTrucks = (state as? StoreInfo)?.foodTrucks.orEmpty().run(::FoodTruckList)

                    SideEffect { if (state is Empty) viewModel.loadTapList(selectedStore) }

                    ListChucksTaps(taps, foodTrucks, selectedStore, state is Loading, onTruckEventSelected) {
                        selectedStore = it.also(viewModel::loadTapList)
                    }
                }
            }
        }
    }
}

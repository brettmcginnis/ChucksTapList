package com.serge.chuckstaplist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.serge.chuckstaplist.TapListViewModel.State.Empty
import com.serge.chuckstaplist.TapListViewModel.State.Loading
import com.serge.chuckstaplist.ui.theme.ChucksTapListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                    val taps by remember { derivedStateOf { (state as? TapListViewModel.State.TapList)?.taps.orEmpty() } }

                    SideEffect { if (state is Empty) viewModel.loadTapList(selectedStore) }

                    ListChucksTaps(taps, selectedStore, state is Loading) { selectedStore = it.also(viewModel::loadTapList) }
                }
            }
        }
    }
}

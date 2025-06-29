package com.serge.chuckstaplist.ui.circuit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.serge.chuckstaplist.ui.components.StoreSelector
import com.slack.circuit.runtime.ui.Ui

class StoreSelectionUi : Ui<StoreSelectionUiState> {
    
    @Composable
    override fun Content(state: StoreSelectionUiState, modifier: Modifier) {
        StoreSelector(state.stores, state.onStoreSelected)
    }
}

fun storeSelectionUiFactory() = Ui.Factory { screen, _ ->
    when (screen) {
        StoreSelectionScreen -> StoreSelectionUi()
        else -> null
    }
}

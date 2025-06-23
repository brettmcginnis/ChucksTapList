package com.serge.chuckstaplist.ui.circuit

import androidx.compose.runtime.Composable
import com.serge.chuckstaplist.ChucksStore
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class StoreSelectionPresenter(
    private val navigator: Navigator
) : Presenter<StoreSelectionUiState> {

    @Composable
    override fun present(): StoreSelectionUiState {
        val stores = listOf(
            ChucksStore.SEWARD_PARK,
            ChucksStore.GREENWOOD,
            ChucksStore.CENTRAL_DISTRICT
        ).toImmutableList()

        return StoreSelectionUiState(
            stores = stores,
            onStoreSelected = { store ->
                navigator.goTo(TapListScreen(store))
            }
        )
    }
}

data class StoreSelectionUiState(
    val stores: ImmutableList<ChucksStore>,
    val onStoreSelected: (ChucksStore) -> Unit
) : CircuitUiState

fun storeSelectionPresenterFactory() = Presenter.Factory { screen, navigator, _ ->
    when (screen) {
        StoreSelectionScreen -> StoreSelectionPresenter(navigator)
        else -> null
    }
}
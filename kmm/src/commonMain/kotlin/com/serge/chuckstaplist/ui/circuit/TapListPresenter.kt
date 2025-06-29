package com.serge.chuckstaplist.ui.circuit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.domain.FoodTruckEvent
import com.serge.chuckstaplist.domain.openUntappdSearch
import com.serge.chuckstaplist.domain.usecases.GetFoodTrucksUseCase
import com.serge.chuckstaplist.domain.usecases.GetTapListUseCase
import com.serge.chuckstaplist.domain.usecases.UseCaseResult
import com.serge.chuckstaplist.platform.ExternalBrowser
import com.serge.chuckstaplist.ui.extensions.colorValue
import com.serge.chuckstaplist.ui.models.ColorFilterSet
import com.serge.chuckstaplist.ui.models.ExpandedTaps
import com.serge.chuckstaplist.ui.models.TAP_LIST_COLUMNS
import com.serge.chuckstaplist.ui.models.TapList
import com.serge.chuckstaplist.ui.models.TapListSortState
import com.serge.chuckstaplist.ui.platform.BackHandler
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class TapListPresenter(
    private val screen: TapListScreen,
    private val navigator: Navigator,
    private val getTapListUseCase: GetTapListUseCase,
    private val getFoodTrucksUseCase: GetFoodTrucksUseCase,
    private val externalBrowser: ExternalBrowser,
) : Presenter<TapListUiState> {

    @Composable
    override fun present(): TapListUiState {
        BackHandler(enabled = true) { navigator.pop() }

        var isRefreshing by rememberRetained { mutableStateOf(true) }
        val foodTruckResult by foodTruckState(isRefreshing)
        val tapListResult by tapListState(isRefreshing, onLoaded = { isRefreshing = false})
        var colorFilterState by rememberSaveable(saver = ColorFilterSet.Saver) { mutableStateOf(ColorFilterSet()) }
        var expandedItems by rememberSaveable(saver = ExpandedTaps.Saver) { mutableStateOf(ExpandedTaps()) }
        var sortState by rememberSaveable(saver = TapListSortState.Saver) {
            mutableStateOf(TapListSortState(0, true, TAP_LIST_COLUMNS[0].sortType))
        }

        val filteredTaps =
            tapListResult?.data
                ?.takeUnless { isRefreshing }
                ?.filter { if (colorFilterState.isEmpty()) true else it.colorValue in colorFilterState }
                ?.sortedWith(sortState)
                .orEmpty()
                .run(::TapList)

        return TapListUiState(
            store = screen.store,
            filteredTaps = filteredTaps,
            foodTrucks = foodTruckResult?.data?.toImmutableList() ?: persistentListOf(),
            isRefreshing = isRefreshing,
            errorMessage = tapListResult?.error?.message,
            colorFilterState = colorFilterState,
            sortState = sortState,
            expandedItems = expandedItems,
            onRefresh = { isRefreshing = true },
            onTapLongPressed = externalBrowser::openUntappdSearch,
            onFoodTruckSelected = { foodTruck -> externalBrowser.openUrl(foodTruck.url) },
            onColorFilterChanged = { newFilterState -> colorFilterState = newFilterState },
            onSortChanged = { column ->
                sortState = when (val index = column.index) {
                    sortState.columnIndex -> sortState.copy(isAscending = !sortState.isAscending)
                    else -> TapListSortState(index, true, column.sortType)
                }
            },
            onTapExpandToggled = { tapNumber ->
                expandedItems = with(expandedItems) { 
                    ExpandedTaps(if (contains(tapNumber)) minus(tapNumber) else plus(tapNumber)) 
                }
            },
            onBackPressed = { navigator.pop() }
        )
    }

    @Composable
    private fun foodTruckState(isRefreshing: Boolean) =
        produceRetainedState<UseCaseResult<List<FoodTruckEvent>>?>(
            initialValue = null,
            key1 = screen.store,
            key2 = isRefreshing
        ) { if (isRefreshing) getFoodTrucksUseCase(screen.store).collect { value = it } }

    @Composable
    private fun tapListState(isRefreshing: Boolean, onLoaded: () -> Unit) =
        produceRetainedState<UseCaseResult<List<TapModel>>?>(
            initialValue = null,
            key1 = screen.store,
            key2 = isRefreshing
        ) {
            if(isRefreshing) {
                getTapListUseCase(screen.store).collect { result ->
                    value = result
                    if (result !is UseCaseResult.Loading) {
                        onLoaded()
                    }
                }
            }
        }
}

data class TapListUiState(
    val store: com.serge.chuckstaplist.ChucksStore,
    val filteredTaps: TapList,
    val foodTrucks: ImmutableList<FoodTruckEvent>,
    val isRefreshing: Boolean,
    val errorMessage: String?,
    val colorFilterState: ColorFilterSet,
    val sortState: TapListSortState,
    val expandedItems: ExpandedTaps,
    val onRefresh: () -> Unit,
    val onTapLongPressed: (TapModel) -> Unit,
    val onFoodTruckSelected: (FoodTruckEvent) -> Unit,
    val onColorFilterChanged: (ColorFilterSet) -> Unit,
    val onSortChanged: (com.serge.chuckstaplist.ui.models.TapListColumn) -> Unit,
    val onTapExpandToggled: (Int) -> Unit,
    val onBackPressed: () -> Unit
) : CircuitUiState

fun tapListPresenterFactory(
    getTapListUseCase: GetTapListUseCase,
    getFoodTrucksUseCase: GetFoodTrucksUseCase,
    externalBrowser: ExternalBrowser,
) = Presenter.Factory { screen, navigator, _ ->
    when (screen) {
        is TapListScreen -> TapListPresenter(screen, navigator, getTapListUseCase, getFoodTrucksUseCase, externalBrowser)
        else -> null
    }
}

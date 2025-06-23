package com.serge.chuckstaplist.ui.circuit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.domain.usecases.GetFoodTrucksUseCase
import com.serge.chuckstaplist.domain.usecases.GetTapListUseCase
import com.serge.chuckstaplist.domain.FoodTruckEvent
import com.serge.chuckstaplist.platform.ExternalBrowser
import com.serge.chuckstaplist.platform.ShakeDetector
import com.serge.chuckstaplist.ui.platform.BackHandler
import com.serge.chuckstaplist.ui.extensions.colorValue
import com.serge.chuckstaplist.ui.models.ColorFilterSet
import com.serge.chuckstaplist.ui.models.ExpandedTaps
import com.serge.chuckstaplist.ui.models.TapList
import com.serge.chuckstaplist.ui.models.TapListSortState
import com.serge.chuckstaplist.ui.models.TAP_LIST_COLUMNS
import androidx.compose.runtime.saveable.rememberSaveable
import com.serge.chuckstaplist.domain.usecases.UseCaseResult
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuitx.effects.LaunchedImpressionEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private const val COLOR_CYCLE_DURATION_MS = 200L
private const val SHAKE_HIGHLIGHT_DURATION_MS = 3000L

class TapListPresenter(
    private val screen: TapListScreen,
    private val navigator: Navigator,
    private val getTapListUseCase: GetTapListUseCase,
    private val getFoodTrucksUseCase: GetFoodTrucksUseCase,
    private val externalBrowser: ExternalBrowser,
    private val shakeDetector: ShakeDetector
) : Presenter<TapListUiState> {

    @Composable
    override fun present(): TapListUiState {
        BackHandler(enabled = true) { navigator.pop() }

        var isRefreshing by rememberRetained { mutableStateOf(true) }
        val tapListResult by produceRetainedState<UseCaseResult<List<TapModel>>?>(
            initialValue = null,
            key1 = screen.store,
            key2 = isRefreshing
        ) {
            if(isRefreshing) {
                getTapListUseCase(screen.store).collect { result ->
                    value = result
                    if (result !is UseCaseResult.Loading) {
                        isRefreshing = false
                    }
                }
            }
        }
        val foodTruckResult by produceRetainedState<UseCaseResult<List<FoodTruckEvent>>?>(
            initialValue = null,
            key1 = screen.store,
            key2 = isRefreshing
        ) { if(isRefreshing) getFoodTrucksUseCase(screen.store).collect { value = it } }
        
        // UI state management moved from UI to presenter
        var colorFilterState by rememberSaveable(saver = ColorFilterSet.Saver) {
            mutableStateOf(ColorFilterSet())
        }
        var sortState by rememberSaveable(saver = TapListSortState.Saver) { 
            mutableStateOf(TapListSortState(0, true, TAP_LIST_COLUMNS[0].sortType)) 
        }
        var expandedItems by rememberSaveable(saver = ExpandedTaps.Saver) { mutableStateOf(ExpandedTaps()) }

        // State for random beer highlighting
        var tapHighlightState by rememberRetained { mutableStateOf(TapHighlightState()) }

        // Apply filtering and sorting logic moved from UI
        val filteredTaps = rememberRetained(tapListResult, isRefreshing, colorFilterState, sortState) {
            tapListResult?.data
                ?.takeUnless { isRefreshing }
                ?.filter { if (colorFilterState.isEmpty()) true else it.colorValue in colorFilterState }
                ?.sortedWith(sortState)
                .orEmpty()
                .run(::TapList)
        }

        // Update expanded items when filtered taps change
        LaunchedImpressionEffect(filteredTaps.size) {
            expandedItems = ExpandedTaps()
        }

        ShakeToHighlightEffect(filteredTaps, tapHighlightState) { tapHighlightState = it }

        return TapListUiState(
            store = screen.store,
            filteredTaps = filteredTaps,
            foodTrucks = foodTruckResult?.data?.toImmutableList() ?: persistentListOf(),
            isRefreshing = isRefreshing,
            errorMessage = tapListResult?.error?.message,
            tapHighlightState = tapHighlightState,
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
            }
        )
    }

    @Composable
    private fun ShakeToHighlightEffect(
        filteredTaps: TapList,
        tapHighlightState: TapHighlightState,
        onStateUpdated: (TapHighlightState) -> Unit
    ) {
        var animationTrigger by rememberRetained { mutableIntStateOf(0) }
        val rememberedCallback by rememberUpdatedState(onStateUpdated)

        LaunchedEffect(animationTrigger) {
            if (animationTrigger > 0) {
                // Cycle through colors for 3 seconds (15 cycles * 200ms each)
                repeat((SHAKE_HIGHLIGHT_DURATION_MS / COLOR_CYCLE_DURATION_MS).toInt()) {
                    rememberedCallback(tapHighlightState.copy(currentColorIndex = it % 5))
                    delay(COLOR_CYCLE_DURATION_MS)
                }

                // Reset after animation
                rememberedCallback(TapHighlightState())
            }
        }

        LaunchedEffect(filteredTaps.size) {
            if (filteredTaps.isNotEmpty()) {
                shakeDetector.shakesFlow(1000).collect {
                    // Start new animation with filtered taps
                    onStateUpdated(
                        TapHighlightState(
                            highlightedTapIndex = Random.nextInt(filteredTaps.size),
                            shouldScrollToHighlighted = true,
                            currentColorIndex = 0
                        ),
                    )

                    // Trigger animation by incrementing the trigger
                    animationTrigger++
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
    val tapHighlightState: TapHighlightState,
    val colorFilterState: ColorFilterSet,
    val sortState: TapListSortState,
    val expandedItems: ExpandedTaps,
    val onRefresh: () -> Unit,
    val onTapLongPressed: (TapModel) -> Unit,
    val onFoodTruckSelected: (FoodTruckEvent) -> Unit,
    val onColorFilterChanged: (ColorFilterSet) -> Unit,
    val onSortChanged: (com.serge.chuckstaplist.ui.models.TapListColumn) -> Unit,
    val onTapExpandToggled: (Int) -> Unit
) : CircuitUiState

fun tapListPresenterFactory(
    getTapListUseCase: GetTapListUseCase,
    getFoodTrucksUseCase: GetFoodTrucksUseCase,
    externalBrowser: ExternalBrowser,
    shakeDetector: ShakeDetector
) = Presenter.Factory { screen, navigator, _ ->
    when (screen) {
        is TapListScreen -> TapListPresenter(screen, navigator, getTapListUseCase, getFoodTrucksUseCase, externalBrowser, shakeDetector)
        else -> null
    }
}

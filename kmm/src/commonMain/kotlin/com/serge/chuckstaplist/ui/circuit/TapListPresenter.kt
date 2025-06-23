package com.serge.chuckstaplist.ui.circuit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.domain.usecases.GetFoodTrucksUseCase
import com.serge.chuckstaplist.domain.usecases.GetTapListUseCase
import com.serge.chuckstaplist.foodtruck.FoodTruckEvent
import com.serge.chuckstaplist.platform.ExternalBrowser
import com.serge.chuckstaplist.platform.ShakeDetector
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private const val SHAKE_DEBOUNCE_MS = 1000L
private const val COLOR_CYCLE_DURATION_MS = 200L

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
        var isRefreshing by remember { mutableStateOf(false) }
        var tapListResult by remember { mutableStateOf<GetTapListUseCase.Result<List<TapModel>>?>(null) }
        var foodTruckResult by remember { mutableStateOf<GetFoodTrucksUseCase.Result<List<FoodTruckEvent>>?>(null) }

        val loadData = {
            isRefreshing = true
        }

        LaunchedEffect(screen.store, isRefreshing) {
            if (tapListResult == null || isRefreshing) {
                getTapListUseCase(screen.store).collect { result ->
                    tapListResult = result
                    if (result !is GetTapListUseCase.Result.Loading) {
                        isRefreshing = false
                    }
                }
            }
        }

        LaunchedEffect(screen.store, isRefreshing) {
            if (foodTruckResult == null || isRefreshing) {
                getFoodTrucksUseCase(screen.store).collect { result ->
                    foodTruckResult = result
                }
            }
        }

        // State for random beer highlighting
        var highlightedTapIndex by remember { mutableStateOf(-1) }
        var currentColorIndex by remember { mutableStateOf(0) }
        var shouldScrollToHighlighted by remember { mutableStateOf(false) }
        var animationJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
        
        val coroutineScope = rememberCoroutineScope()
        
        val onShakeDetected = { filteredTaps: List<TapModel> ->
            if (filteredTaps.isNotEmpty()) {
                // Cancel any existing animation
                animationJob?.cancel()
                
                // Start new animation with filtered taps
                highlightedTapIndex = Random.nextInt(filteredTaps.size)
                shouldScrollToHighlighted = true
                currentColorIndex = 0
                
                animationJob = coroutineScope.launch {
                    // Cycle through colors for 3 seconds (15 cycles * 200ms each)
                    repeat(15) {
                        currentColorIndex = it % 5 // 5 colors in the animation
                        delay(COLOR_CYCLE_DURATION_MS)
                    }
                    
                    // Reset after animation
                    highlightedTapIndex = -1
                    shouldScrollToHighlighted = false
                    animationJob = null
                }
            }
        }
        
        val taps = when (val result = tapListResult) {
            is GetTapListUseCase.Result.Success -> result.data.toImmutableList()
            else -> persistentListOf()
        }
        
        val foodTrucks = when (val result = foodTruckResult) {
            is GetFoodTrucksUseCase.Result.Success -> result.data.toImmutableList()
            else -> persistentListOf()
        }

        val isLoading = tapListResult is GetTapListUseCase.Result.Loading || 
                       foodTruckResult is GetFoodTrucksUseCase.Result.Loading ||
                       tapListResult == null

        val errorMessage = when (val result = tapListResult) {
            is GetTapListUseCase.Result.Error -> result.exception.message
            else -> null
        }

        return TapListUiState(
            store = screen.store,
            taps = taps,
            foodTrucks = foodTrucks,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            errorMessage = errorMessage,
            highlightedTapIndex = highlightedTapIndex,
            currentColorIndex = currentColorIndex,
            shouldScrollToHighlighted = shouldScrollToHighlighted,
            shakeDetector = shakeDetector,
            onShakeDetected = onShakeDetected,
            onRefresh = loadData,
            onBackPressed = {
                navigator.pop()
            },
            onTapSelected = { tap ->
                // Regular tap opens Untappd search
                externalBrowser.openUntappdSearch(tap)
            },
            onTapLongPressed = { tap ->
                // Long press also opens Untappd search
                externalBrowser.openUntappdSearch(tap)
            },
            onFoodTruckSelected = { foodTruck ->
                externalBrowser.openUrl(foodTruck.url)
            }
        )
    }
}

data class TapListUiState(
    val store: com.serge.chuckstaplist.ChucksStore,
    val taps: ImmutableList<TapModel>,
    val foodTrucks: ImmutableList<FoodTruckEvent>,
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val errorMessage: String?,
    val highlightedTapIndex: Int = -1,
    val currentColorIndex: Int = 0,
    val shouldScrollToHighlighted: Boolean = false,
    val shakeDetector: ShakeDetector,
    val onShakeDetected: (List<TapModel>) -> Unit,
    val onRefresh: () -> Unit,
    val onBackPressed: () -> Unit,
    val onTapSelected: (TapModel) -> Unit,
    val onTapLongPressed: (TapModel) -> Unit,
    val onFoodTruckSelected: (FoodTruckEvent) -> Unit
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

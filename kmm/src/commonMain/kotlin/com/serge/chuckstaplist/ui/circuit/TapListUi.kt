package com.serge.chuckstaplist.ui.circuit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.serge.chuckstaplist.ui.components.ColorFilterHeader
import com.serge.chuckstaplist.ui.components.FoodTruckRow
import com.serge.chuckstaplist.ui.components.TapListHeader
import com.serge.chuckstaplist.ui.components.tapItem
import com.serge.chuckstaplist.ui.extensions.colorValue
import com.serge.chuckstaplist.ui.models.ColorFilterSet
import com.serge.chuckstaplist.ui.models.ExpandedTaps
import com.serge.chuckstaplist.ui.models.TAP_LIST_COLUMNS
import com.serge.chuckstaplist.ui.models.TapList
import com.serge.chuckstaplist.ui.models.TapListSortState
import com.serge.chuckstaplist.ui.platform.BackHandler
import com.serge.chuckstaplist.ui.theme.DarkGray
import com.serge.chuckstaplist.ui.theme.Gray
import com.serge.chuckstaplist.ui.theme.Green
import com.serge.chuckstaplist.ui.theme.Orange
import com.serge.chuckstaplist.ui.theme.Pink
import com.serge.chuckstaplist.ui.theme.Red
import com.serge.chuckstaplist.ui.theme.Yellow
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.ui.Ui
private const val HEADER_ITEMS_COUNT = 4 // Store title + color filter + food trucks + sticky header
private val SHAKE_ANIMATION_COLORS = listOf(Red, Orange, Yellow, Green, Pink)

@OptIn(ExperimentalMaterialApi::class)
class TapListUi : Ui<TapListUiState> {
    
    @Composable
    override fun Content(state: TapListUiState, modifier: Modifier) {
        BackHandler(enabled = true, onBack = state.onBackPressed)
        
        val (colorFilterState, setColorFilterState) = rememberSaveable(
            saver = ColorFilterSet.Saver
        ) { mutableStateOf(ColorFilterSet()) }
        
        val scrollState = rememberLazyListState()
        val density = LocalDensity.current
        var sortState by rememberRetained {
            mutableStateOf(TapListSortState(0, true, TAP_LIST_COLUMNS[0].sortType)) 
        }
        
        val filteredTaps = state.taps.takeUnless { state.isLoading }
            ?.filter { if (colorFilterState.isEmpty()) true else it.colorValue in colorFilterState }
            ?.sortedWith(sortState)
            .orEmpty()
            .run(::TapList)

        var expandedItems by rememberSaveable(
            filteredTaps.size,
            saver = ExpandedTaps.Saver
        ) { mutableStateOf(ExpandedTaps()) }

        val pullRefreshState = rememberPullRefreshState(state.isLoading, state.onRefresh)
        
        // Handle shake events with filtered taps
        LaunchedEffect(filteredTaps.size) {
            state.shakeDetector.shakesFlow(1000).collect {
                state.onShakeDetected(filteredTaps.toList())
            }
        }
        
        BoxWithConstraints(
            modifier
                .pullRefresh(pullRefreshState)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // Handle scroll to highlighted item with dynamic centering
            LaunchedEffect(state.shouldScrollToHighlighted, state.highlightedTapIndex) {
                if (state.shouldScrollToHighlighted && state.highlightedTapIndex >= 0) {
                    val itemIndex = state.highlightedTapIndex + HEADER_ITEMS_COUNT
                    
                    // Calculate center offset based on available height
                    val availableHeightPx = with(density) { maxHeight.toPx() }
                    val centerOffset = -(availableHeightPx / 2).toInt()
                    
                    scrollState.animateScrollToItem(itemIndex, centerOffset)
                }
            }
            LazyColumn(Modifier.fillMaxSize(), scrollState) {
                item {
                    Text(
                        "${state.store.storeName} Taplist",
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center
                    )
                }
                
                item { 
                    ColorFilterHeader(colorFilterState, setColorFilterState) 
                }
                
                item { 
                    AnimatedVisibility(state.foodTrucks.isNotEmpty()) { 
                        FoodTruckRow(
                            com.serge.chuckstaplist.ui.models.FoodTruckList(state.foodTrucks), 
                            state.onFoodTruckSelected
                        ) 
                    } 
                }
                
                stickyHeader {
                    TapListHeader(TAP_LIST_COLUMNS, sortState) { column ->
                        sortState = when (val index = column.index) {
                            sortState.columnIndex -> sortState.copy(isAscending = !sortState.isAscending)
                            else -> TapListSortState(index, true, column.sortType)
                        }
                    }
                }

                filteredTaps.forEachIndexed { index, tap ->
                    val bgColor = if (index % 2 == 0) DarkGray else Color.Black
                    
                    // Highlighting logic for shake detection
                    val borderColor = if (state.highlightedTapIndex == index) {
                        SHAKE_ANIMATION_COLORS.getOrElse(state.currentColorIndex) { Gray }
                    } else {
                        Gray
                    }
                    
                    tapItem(
                        tap = tap,
                        isExpanded = expandedItems.contains(tap.tapNumber),
                        bgColor = bgColor,
                        borderColor = borderColor,
                        colWeights = TAP_LIST_COLUMNS.weights,
                        onClick = { beer ->
                            val tapNumber = beer.tapNumber
                            expandedItems = with(expandedItems) { 
                                ExpandedTaps(if (contains(tapNumber)) minus(tapNumber) else plus(tapNumber)) 
                            }
                        },
                        onLongClick = state.onTapLongPressed
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = state.isLoading, 
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

fun tapListUiFactory() = Ui.Factory { screen, _ ->
    when (screen) {
        is TapListScreen -> TapListUi()
        else -> null
    }
}

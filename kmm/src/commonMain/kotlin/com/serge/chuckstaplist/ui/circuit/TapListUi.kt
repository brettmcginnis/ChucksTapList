package com.serge.chuckstaplist.ui.circuit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.serge.chuckstaplist.ui.components.ColorFilterHeader
import com.serge.chuckstaplist.ui.components.FoodTruckRow
import com.serge.chuckstaplist.ui.components.TapListHeader
import com.serge.chuckstaplist.ui.components.tapItem
import com.serge.chuckstaplist.ui.models.TAP_LIST_COLUMNS
import com.serge.chuckstaplist.ui.theme.DarkGray
import com.serge.chuckstaplist.ui.theme.Gray
import com.serge.chuckstaplist.ui.theme.Green
import com.serge.chuckstaplist.ui.theme.Orange
import com.serge.chuckstaplist.ui.theme.Pink
import com.serge.chuckstaplist.ui.theme.Red
import com.serge.chuckstaplist.ui.theme.Yellow
import com.slack.circuit.runtime.ui.Ui

private const val HEADER_ITEMS_COUNT = 4 // Store title + color filter + food trucks + sticky header
private val SHAKE_ANIMATION_COLORS = listOf(Red, Orange, Yellow, Green, Pink)

@OptIn(ExperimentalMaterialApi::class)
class TapListUi : Ui<TapListUiState> {
    
    @Composable
    override fun Content(state: TapListUiState, modifier: Modifier) {
        val pullRefreshState = rememberPullRefreshState(state.isRefreshing, state.onRefresh)
        val scrollState = rememberLazyListState()

        BoxWithConstraints(
            modifier
                .pullRefresh(pullRefreshState)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            HighlightItemEffect(state.tapHighlightState, scrollState)

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
                
                item { ColorFilterHeader(state.colorFilterState, state.onColorFilterChanged) }
                
                item { 
                    AnimatedVisibility(state.foodTrucks.isNotEmpty()) { 
                        FoodTruckRow(
                            com.serge.chuckstaplist.ui.models.FoodTruckList(state.foodTrucks), 
                            state.onFoodTruckSelected
                        ) 
                    } 
                }
                
                stickyHeader {
                    TapListHeader(TAP_LIST_COLUMNS, state.sortState, state.onSortChanged)
                }

                state.filteredTaps.forEachIndexed { index, tap ->
                    val bgColor = if (index % 2 == 0) DarkGray else Color.Black
                    
                    val borderColor = with(state.tapHighlightState) {
                        if(highlightedTapIndex == index) SHAKE_ANIMATION_COLORS.getOrElse(currentColorIndex) { Gray } else Gray
                    }

                    tapItem(
                        tap = tap,
                        isExpanded = state.expandedItems.contains(tap.tapNumber),
                        bgColor = bgColor,
                        borderColor = borderColor,
                        colWeights = TAP_LIST_COLUMNS.weights,
                        onClick = { beer -> state.onTapExpandToggled(beer.tapNumber) },
                        onLongClick = state.onTapLongPressed
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.HighlightItemEffect(tapHighlightState: TapHighlightState, lazyListState: LazyListState) {
    val density = LocalDensity.current

    LaunchedEffect(tapHighlightState) {
        if (tapHighlightState.shouldScrollToHighlighted && tapHighlightState.highlightedTapIndex >= 0) {
            val itemIndex = tapHighlightState.highlightedTapIndex + HEADER_ITEMS_COUNT

            // Calculate center offset based on available height
            val availableHeightPx = with(density) { maxHeight.toPx() }
            val centerOffset = -(availableHeightPx / 2).toInt()

            lazyListState.animateScrollToItem(itemIndex, centerOffset)
        }
    }
}

fun tapListUiFactory() = Ui.Factory { screen, _ ->
    when (screen) {
        is TapListScreen -> TapListUi()
        else -> null
    }
}

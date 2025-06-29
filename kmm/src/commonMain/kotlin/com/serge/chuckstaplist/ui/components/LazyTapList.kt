package com.serge.chuckstaplist.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serge.chuckstaplist.platform.ShakeDetector
import com.serge.chuckstaplist.ui.circuit.TapListUiState
import com.serge.chuckstaplist.ui.models.FoodTruckList
import com.serge.chuckstaplist.ui.models.TAP_LIST_COLUMNS
import com.serge.chuckstaplist.ui.models.TapList
import com.serge.chuckstaplist.ui.platform.isIOS
import com.serge.chuckstaplist.ui.theme.DarkGray
import com.serge.chuckstaplist.ui.theme.Green
import com.serge.chuckstaplist.ui.theme.Orange
import com.serge.chuckstaplist.ui.theme.Pink
import com.serge.chuckstaplist.ui.theme.Red
import com.serge.chuckstaplist.ui.theme.Yellow
import com.slack.circuit.retained.rememberRetained
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

private const val HEADER_ITEMS_COUNT = 4 // Store title + color filter + food trucks + sticky header
private val SHAKE_ANIMATION_COLORS = listOf(Red, Orange, Yellow, Green, Pink)
private const val COLOR_CYCLE_DURATION_MS = 200L
private const val SHAKE_HIGHLIGHT_DURATION_MS = 3000L

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyTapList(
    state: TapListUiState,
    showTutorial: Boolean,
    shakeDetector: ShakeDetector,
    modifier: Modifier,
    onShowTutorial: (Boolean) -> Unit
) {
    val scrollState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(state.isRefreshing, state.onRefresh)

    BoxWithConstraints(
        modifier
            .run { if(showTutorial) this else pullRefresh(pullRefreshState) }
            .windowInsetsPadding(
                WindowInsets.safeDrawing
                    .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
            )
    ) {
        val highlightedTapState by highlightedTapState(state.filteredTaps, scrollState, shakeDetector)
        val (highlightedTap, colorIndex) = highlightedTapState

        LazyColumn(Modifier.fillMaxSize(), scrollState) {
            item { TitleHeaderItem(state, onShowTutorial) }
            item { ColorFilterHeader(state.colorFilterState, state.onColorFilterChanged) }
            item {
                AnimatedVisibility(state.foodTrucks.isNotEmpty()) {
                    FoodTruckRow(FoodTruckList(state.foodTrucks), state.onFoodTruckSelected)
                }
            }
            stickyHeader { TapListHeader(TAP_LIST_COLUMNS, state.sortState, state.onSortChanged) }

            state.filteredTaps.forEachIndexed { index, tap ->
                val bgColor = if (index % 2 == 0) DarkGray else Color.Black

                tapItem(
                    tap = tap,
                    isExpanded = state.expandedItems.contains(tap.tapNumber),
                    bgColor = bgColor,
                    borderColor = SHAKE_ANIMATION_COLORS.takeIf { highlightedTap == tap.tapNumber }?.getOrNull(colorIndex),
                    colWeights = TAP_LIST_COLUMNS.weights,
                    onClick = { beer -> state.onTapExpandToggled(beer.tapNumber) },
                    onLongClick = state.onTapLongPressed
                )
            }

            item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars)) }
        }

        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        TutorialOverlay(
            isVisible = showTutorial,
            modifier = modifier,
            onDismiss = { onShowTutorial(false) }
        )
    }
}

@Composable
private fun TitleHeaderItem(state: TapListUiState, onShowTutorial: (Boolean) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        if (isIOS) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Back",
                modifier =
                    Modifier.clickable { state.onBackPressed() }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                tint = Color.White
            )
        }
        Text(
            "${state.store.storeName} Taplist",
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
            color = Color.White,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
            contentDescription = "Tutorial",
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onShowTutorial(true) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            tint = Color.White
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.highlightedTapState(
    filteredTaps: TapList,
    lazyListState: LazyListState,
    shakeDetector: ShakeDetector,
) : MutableState<HighlightState> {
    val density = LocalDensity.current
    val rememberedTaps by rememberUpdatedState(filteredTaps)
    var animationTrigger by rememberRetained { mutableIntStateOf(0) }
    val highlightedState = rememberRetained { mutableStateOf(HighlightState()) }
    val highlightIndex = filteredTaps.indexOfFirst { it.tapNumber == highlightedState.value.tapNumber }

    LaunchedEffect(Unit) {
        shakeDetector.shakesFlow(1000).collect {
            with(highlightedState) {
                value = value.copy(tapNumber = rememberedTaps[Random.nextInt(rememberedTaps.size)].tapNumber)
            }
        }
    }

    LaunchedEffect(highlightIndex) {
        if (highlightIndex >= 0) {
            // Calculate center offset based on available height
            val availableHeightPx = with(density) { maxHeight.toPx() }
            val centerOffset = -(availableHeightPx / 2).toInt()

            animationTrigger++
            lazyListState.animateScrollToItem(highlightIndex + HEADER_ITEMS_COUNT, centerOffset)
        }
    }

    LaunchedEffect(animationTrigger) {
        if (animationTrigger > 0) {
            // Cycle through colors for 3 seconds (15 cycles * 200ms each)
            repeat((SHAKE_HIGHLIGHT_DURATION_MS / COLOR_CYCLE_DURATION_MS).toInt()) {
                with(highlightedState) { value = value.copy(colorIndex = it % SHAKE_ANIMATION_COLORS.size) }
                delay(COLOR_CYCLE_DURATION_MS)
            }
        }
        withContext(NonCancellable) { highlightedState.value = HighlightState() }
    }

    return highlightedState
}

private data class HighlightState(val tapNumber: Int = -1, val colorIndex: Int = -1)

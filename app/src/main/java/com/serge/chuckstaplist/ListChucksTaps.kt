package com.serge.chuckstaplist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.ui.theme.ChucksTapListTheme
import com.serge.chuckstaplist.ui.theme.colorSetStateSaver

private const val SCROLL_OFFSET_HIDE_HEADER = 50

private val TAP_LIST_COLUMNS = listOf(
    TapListColumn(0, "#", .5f, TapListSortState.Type.TAP),
    TapListColumn(1, "Beer", 3f, TapListSortState.Type.NAME),
    TapListColumn(2, "Price", 1.2f, TapListSortState.Type.PRICE),
    TapListColumn(3, "Origin", .9f, TapListSortState.Type.ORIGIN),
    TapListColumn(4, "ABV%", 1f, TapListSortState.Type.ABV),
)
private val List<TapListColumn>.weights get() = map { it.weight }.toFloatArray()

@Composable
fun ListChucksTaps(
    taps: List<TapModel>,
    selectedStore: ChucksStore = ChucksStore.GREENWOOD,
    isLoading: Boolean = false,
    onStoreSelected: (ChucksStore) -> Unit,
) = Column {
    val density = LocalDensity.current
    val screenHeight = LocalConfiguration.current.screenHeightDp

    var colorFilterState by rememberSaveable(saver = colorSetStateSaver) { mutableStateOf(emptySet()) }
    val scrollState = rememberLazyListState()
    var sortState by remember { mutableStateOf(TapListSortState(0, true, TAP_LIST_COLUMNS[0].sortType)) }

    val shouldShowTopBar by remember {
        derivedStateOf {
            with(scrollState) {
                val avgItemHeight = with(layoutInfo.visibleItemsInfo) { if (isEmpty()) 0 else sumOf { it.size } / size }
                val listFitsOnScreen = with(density) { avgItemHeight.toDp() } * layoutInfo.totalItemsCount < screenHeight.dp
                val firstItemIsVisible = firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset < SCROLL_OFFSET_HIDE_HEADER
                firstItemIsVisible || listFitsOnScreen
            }
        }
    }

    AnimatedVisibility(shouldShowTopBar) {
        HeaderTopBar(colorFilterState, { StoreSelector(selectedStore, onStoreSelected) }) {
            colorFilterState = it
        }
    }

    TapListHeader(TAP_LIST_COLUMNS, sortState) {
        sortState = when (val index = it.index) {
            sortState.columnIndex -> sortState.copy(isAscending = !sortState.isAscending)
            else -> TapListSortState(index, true, it.sortType)
        }
    }

    var expandedItems by rememberSaveable { mutableStateOf(emptySet<Int>()) }
    if (isLoading) expandedItems = emptySet()

    SwipeRefresh(
        rememberSwipeRefreshState(isLoading),
        onRefresh = { onStoreSelected(selectedStore) },
    ) {
        val transformedState = taps
            .takeUnless { isLoading }
            ?.filter { if (colorFilterState.isEmpty()) true else it.colorValue in colorFilterState }
            ?.sortedWith(sortState)
            .orEmpty()

        LazyTapList(transformedState, scrollState, expandedItems, TAP_LIST_COLUMNS.weights) { beer ->
            val tapNumber = beer.tapNumber
            expandedItems = with(expandedItems) { if (contains(tapNumber)) minus(tapNumber) else plus(tapNumber) }
        }
    }
}

@Composable
private fun HeaderTopBar(
    colorFilterSet: Set<Color>,
    storeSelector: @Composable () -> Unit,
    onFilterStateUpdated: (Set<Color>) -> Unit
) = Column {
    val colorFilterSetState by rememberUpdatedState(colorFilterSet)
    val onFilterStateUpdatedState by rememberUpdatedState(onFilterStateUpdated)

    fun updateColorFilterState(selectedColor: Color) {
        with(colorFilterSetState) {
            if (contains(selectedColor)) minus(selectedColor) else plus(selectedColor)
        }.run(onFilterStateUpdatedState)
    }

    if (isLandscape()) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceAround, Alignment.CenterVertically) {
            storeSelector()
            ColorFilterRow(colorFilterSetState, ::updateColorFilterState)
        }
    } else {
        Box(Modifier.fillMaxWidth(), Alignment.Center) { storeSelector() }

        Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
            ColorFilterRow(colorFilterSetState, ::updateColorFilterState)
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun TapListPreview() {
    ChucksTapListTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            listOf(
                TapModel(1, "Duchesse"),
                TapModel(2, "Some Really Really Long Beer Name Imprerial Stout")
            ).let { ListChucksTaps(it) {} }
        }
    }
}

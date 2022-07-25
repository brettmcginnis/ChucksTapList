package com.serge.chuckstaplist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.extensions.addDays
import com.serge.chuckstaplist.foodtruck.FoodTruckEvent
import com.serge.chuckstaplist.foodtruck.FoodTruckRow
import com.serge.chuckstaplist.ui.theme.ChucksTapListTheme
import java.util.Date

private const val SCROLL_OFFSET_HIDE_HEADER = 50

private val TAP_LIST_COLUMNS = listOf(
    TapListColumn(0, "#", .5f, TapListSortState.Type.TAP),
    TapListColumn(1, "Beer", 3f, TapListSortState.Type.NAME),
    TapListColumn(2, "Price", 1.2f, TapListSortState.Type.PRICE),
    TapListColumn(3, "Origin", .9f, TapListSortState.Type.ORIGIN),
    TapListColumn(4, "ABV%", 1f, TapListSortState.Type.ABV),
).run(::TapListColumns)

@Immutable
class TapList(taps: List<TapModel>) : List<TapModel> by taps

@Immutable
class FoodTruckList(foodTrucks: List<FoodTruckEvent>) : List<FoodTruckEvent> by foodTrucks

@Composable
fun ListChucksTaps(
    taps: TapList,
    foodTrucks: FoodTruckList = FoodTruckList(emptyList()),
    selectedStore: ChucksStore = ChucksStore.GREENWOOD,
    isLoading: Boolean = false,
    onTruckEventSelected: (FoodTruckEvent) -> Unit = {},
    onStoreSelected: (ChucksStore) -> Unit,
) = Column {
    val density = LocalDensity.current
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val (colorFilterState, setColorFilterState) = rememberSaveable(saver = ColorFilterSet.Saver) { mutableStateOf(ColorFilterSet()) }
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
        ColorFilterHeader(colorFilterState, setColorFilterState) {
            StoreSelector(selectedStore, onStoreSelected)
        }
    }

    AnimatedVisibility(foodTrucks.isNotEmpty()) {
        FoodTruckRow(foodTrucks, onTruckEventSelected)
    }

    TapListHeader(TAP_LIST_COLUMNS, sortState) {
        sortState = when (val index = it.index) {
            sortState.columnIndex -> sortState.copy(isAscending = !sortState.isAscending)
            else -> TapListSortState(index, true, it.sortType)
        }
    }

    var expandedItems by rememberSaveable(saver = ExpandedTaps.Saver) { mutableStateOf(ExpandedTaps()) }

    SwipeRefresh(
        rememberSwipeRefreshState(isLoading),
        onRefresh = { onStoreSelected(selectedStore) },
    ) {
        val transformedState = taps
            .takeUnless { isLoading }
            ?.filter { if (colorFilterState.isEmpty()) true else it.colorValue in colorFilterState }
            ?.sortedWith(sortState)
            .orEmpty()
            .run(::TapList)

        LazyTapList(transformedState, scrollState, expandedItems, TAP_LIST_COLUMNS.weights) { beer ->
            val tapNumber = beer.tapNumber
            expandedItems = with(expandedItems) { ExpandedTaps(if (contains(tapNumber)) minus(tapNumber) else plus(tapNumber)) }
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
            val foodTrucks = listOf(
                FoodTruckEvent("Grilled Cheese Experience", "http://grilledcheeseseattle.com/", Date()),
                FoodTruckEvent("Where Ya At Matt", "https://www.whereyaatmatt.com/", Date().addDays(1)),
                FoodTruckEvent("El Camion", "http://elcamionseattle.com/", Date().addDays(2))
            ).run(::FoodTruckList)
            listOf(
                TapModel(1, "Duchesse"),
                TapModel(2, "Some Really Really Long Beer Name Imperial Stout")
            ).run(::TapList).let { ListChucksTaps(it, foodTrucks = foodTrucks) {} }
        }
    }
}

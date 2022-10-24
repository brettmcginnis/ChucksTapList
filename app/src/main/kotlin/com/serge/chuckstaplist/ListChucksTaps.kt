package com.serge.chuckstaplist

import android.hardware.SensorManager
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.compose.effects.ScrollToEffect
import com.serge.chuckstaplist.compose.whileResumed
import com.serge.chuckstaplist.foodtruck.FoodTruckEvent
import com.serge.chuckstaplist.foodtruck.FoodTruckRow
import com.serge.chuckstaplist.ui.theme.ChucksTapListTheme
import com.serge.chuckstaplist.ui.theme.DarkGray
import com.serge.chuckstaplist.ui.theme.Gray
import com.serge.chuckstaplist.ui.theme.Green
import com.serge.chuckstaplist.ui.theme.Orange
import com.serge.chuckstaplist.ui.theme.Pink
import com.serge.chuckstaplist.ui.theme.Red
import com.serge.chuckstaplist.ui.theme.Yellow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.time.Duration.Companion.days

private const val MIN_MS_BETWEEN_SHAKES = 1_000L
private const val SUB_ANIMATION_DURATION = 200
private const val NUM_COLOR_CYCLE_REPETITIONS = 3
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListChucksTaps(
    taps: TapList,
    foodTrucks: FoodTruckList = FoodTruckList(emptyList()),
    selectedStore: ChucksStore = ChucksStore.GREENWOOD,
    isLoading: Boolean = false,
    onTruckEventSelected: (FoodTruckEvent) -> Unit = {},
    onRefresh: () -> Unit,
) {
    val (colorFilterState, setColorFilterState) = rememberSaveable(saver = ColorFilterSet.Saver) { mutableStateOf(ColorFilterSet()) }
    val scrollState = rememberLazyListState()
    var sortState by rememberSaveable { mutableStateOf(TapListSortState(0, true, TAP_LIST_COLUMNS[0].sortType)) }
    var expandedItems by rememberSaveable(saver = ExpandedTaps.Saver) { mutableStateOf(ExpandedTaps()) }

    val highlightedIndexState = rememberSaveable(taps.size) { mutableStateOf(-1) }
    val animatableColor = remember(highlightedIndexState.value) { Animatable(Gray) }
    ShakeScrollToRandomTap(taps, scrollState, highlightedIndexState) {
        val colorsToAnimate = listOf(Red, Orange, Yellow, Green, Pink)
        repeat(NUM_COLOR_CYCLE_REPETITIONS) {
            colorsToAnimate.forEach { color -> animatableColor.animateTo(color, tween(SUB_ANIMATION_DURATION)) }
        }
        animatableColor.animateTo(Gray, tween(SUB_ANIMATION_DURATION))
    }

    SwipeRefresh(
        rememberSwipeRefreshState(isLoading),
        onRefresh = onRefresh,
    ) {
        LazyColumn(Modifier.fillMaxSize(), scrollState) {
            item {
                Text(
                    "${selectedStore.storeName} Taplist",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center
                )
            }
            item { ColorFilterHeader(colorFilterState, setColorFilterState) }
            item { AnimatedVisibility(foodTrucks.isNotEmpty()) { FoodTruckRow(foodTrucks, onTruckEventSelected) } }
            stickyHeader {
                TapListHeader(TAP_LIST_COLUMNS, sortState) {
                    sortState = when (val index = it.index) {
                        sortState.columnIndex -> sortState.copy(isAscending = !sortState.isAscending)
                        else -> TapListSortState(index, true, it.sortType)
                    }
                }
            }
            taps.takeUnless { isLoading }
                ?.filter { if (colorFilterState.isEmpty()) true else it.colorValue in colorFilterState }
                ?.sortedWith(sortState)
                .orEmpty()
                .run(::TapList)
                .forEachIndexed { index, tap ->
                    val bgColor = if (index % 2 == 0) DarkGray else Color.Black
                    val borderColor = if (index == highlightedIndexState.value) animatableColor.value else Gray
                    tapItem(tap, expandedItems.contains(tap.tapNumber), bgColor, borderColor, TAP_LIST_COLUMNS.weights) { beer ->
                        val tapNumber = beer.tapNumber
                        expandedItems = with(expandedItems) { ExpandedTaps(if (contains(tapNumber)) minus(tapNumber) else plus(tapNumber)) }
                    }
                }
        }
    }
}

@Composable
fun ShakeScrollToRandomTap(
    taps: TapList,
    scrollState: LazyListState,
    highlightedIndexState: MutableState<Int>,
    onScroll: suspend (Int) -> Unit
) {
    var highlightedIndex by highlightedIndexState
    val pickRandomIndex by rememberUpdatedState { highlightedIndex = if (taps.isEmpty()) -1 else Random.nextInt(taps.size) }
    val rememberOnScroll by rememberUpdatedState(onScroll)
    val sensorManager = LocalContext.current.getSystemService<SensorManager>()
    LocalLifecycleOwner.current.lifecycle.whileResumed {
        sensorManager?.shakesFlow(MIN_MS_BETWEEN_SHAKES)?.collect { pickRandomIndex() }
    }
    ScrollToEffect(scrollState, highlightedIndex) {
        rememberOnScroll(highlightedIndex)
        highlightedIndex = -1 // reset highlighted index after animation is done
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
            val now = Clock.System.now()
            val foodTrucks = listOf(
                FoodTruckEvent("Grilled Cheese Experience", "http://grilledcheeseseattle.com/", now.toLocalDateTime(TimeZone.UTC)),
                FoodTruckEvent("Where Ya At Matt", "https://www.whereyaatmatt.com/", now.plus(1.days).toLocalDateTime(TimeZone.UTC)),
                FoodTruckEvent("El Camion", "http://elcamionseattle.com/", now.plus(2.days).toLocalDateTime(TimeZone.UTC))
            ).run(::FoodTruckList)
            listOf(
                TapModel(1, "Duchesse"),
                TapModel(2, "Some Really Really Long Beer Name Imperial Stout")
            ).run(::TapList).let { ListChucksTaps(it, foodTrucks = foodTrucks) {} }
        }
    }
}

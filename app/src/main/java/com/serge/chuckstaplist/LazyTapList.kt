package com.serge.chuckstaplist

import android.hardware.SensorManager
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.effects.ScrollToEffect
import com.serge.chuckstaplist.ui.theme.DarkGray
import com.serge.chuckstaplist.ui.theme.Gray
import com.serge.chuckstaplist.ui.theme.Green
import com.serge.chuckstaplist.ui.theme.Orange
import com.serge.chuckstaplist.ui.theme.Pink
import com.serge.chuckstaplist.ui.theme.Red
import com.serge.chuckstaplist.ui.theme.Yellow
import kotlin.random.Random

private const val MIN_MS_BETWEEN_SHAKES = 1_000L
private const val SUB_ANIMATION_DURATION = 200
private const val NUM_COLOR_CYCLE_REPETITIONS = 3

@Immutable
class ExpandedTaps(expandedTaps: Set<Int> = emptySet()) : Set<Int> by expandedTaps {
    companion object {
        val Saver = Saver<MutableState<ExpandedTaps>, Set<Int>>(
            save = { state -> state.value.toSet() },
            restore = { taps -> mutableStateOf(ExpandedTaps(taps)) },
        )
    }
}

@Composable
fun LazyTapList(
    list: TapList,
    state: LazyListState = rememberLazyListState(),
    expandedItems: ExpandedTaps,
    colWeights: TapListColumns.Weights,
    onClick: (TapModel) -> Unit
) {
    val sensorManager = LocalContext.current.getSystemService<SensorManager>()
    var highlightedIndex by rememberSaveable(list.size) { mutableStateOf(-1) }
    val pickRandomIndex by rememberUpdatedState { highlightedIndex = if (list.isEmpty()) -1 else Random.nextInt(list.size) }
    LaunchedEffect(sensorManager) { sensorManager?.shakesFlow(MIN_MS_BETWEEN_SHAKES)?.collect { pickRandomIndex() } }

    val animatableColor = remember(highlightedIndex) { Animatable(Gray) }
    ScrollToEffect(state, highlightedIndex) {
        val colorsToAnimate = listOf(Red, Orange, Yellow, Green, Pink)
        repeat(NUM_COLOR_CYCLE_REPETITIONS) { colorsToAnimate.forEach { animatableColor.animateTo(it, tween(SUB_ANIMATION_DURATION)) } }
        animatableColor.animateTo(Gray, tween(SUB_ANIMATION_DURATION))
        highlightedIndex = -1 // reset highlighted index after animation is done
    }

    LazyColumn(Modifier.fillMaxSize(), state) {
        list.forEachIndexed { index, tap ->
            val bgColor = if (index % 2 == 0) DarkGray else Color.Black
            val borderColor = if (index == highlightedIndex) animatableColor.value else Gray
            tapItem(tap, expandedItems.contains(tap.tapNumber), bgColor, borderColor, colWeights, onClick)
        }
    }
}

@Suppress("LongParameterList")
@OptIn(ExperimentalAnimationApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
private fun LazyListScope.tapItem(
    tap: TapModel,
    isExpanded: Boolean = false,
    bgColor: Color,
    borderColor: Color,
    colWeights: TapListColumns.Weights,
    onClick: (TapModel) -> Unit
) = item {
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(2.dp)
            .border(2.dp, borderColor)
            .padding(2.dp)
            .combinedClickable(onLongClick = { context.openUntappdSearch(tap) }) { onClick(tap) }
            .animateItemPlacement()
    ) {
        Row(Modifier, Arrangement.Center, Alignment.CenterVertically) {
            TapMainInfo(tap, tap.colorValue, colWeights)
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(expandFrom = Alignment.CenterVertically, clip = false) + scaleIn(initialScale = .3f) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.CenterVertically, clip = false) + scaleOut(targetScale = .3f) + fadeOut()
        ) {
            Column {
                val textModifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .weight(1f)
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    Text("Type: ${tap.type ?: "Other"}", textModifier, textAlign = TextAlign.End)
                    Text("Serving Size: ${tap.serving ?: "???"}", textModifier, textAlign = TextAlign.Start)
                }
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    if (tap.priceOz > 0) {
                        Text(text = "Price/Oz: ${"$%.2f".format(tap.priceOz)}", textModifier, textAlign = TextAlign.End)
                    }
                    if (tap.costOz > 0) {
                        Text(text = "Cost/Oz: ${"$%.2f".format(tap.costOz)}", textModifier, textAlign = TextAlign.Start)
                    }
                }
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    if (tap.growlerCost > 0) {
                        Text(text = "Growler: ${"$%.2f".format(tap.growlerCost)}", textModifier, textAlign = TextAlign.End)
                    }
                    if (tap.crowlerCost > 0) {
                        Text(text = "Crowler: ${"$%.2f".format(tap.crowlerCost)}", textModifier, textAlign = TextAlign.Start)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.TapMainInfo(
    tap: TapModel,
    color: Color,
    colWeights: TapListColumns.Weights
) = tap.info.forEachIndexed { index, beerColumnValue ->
    val alignment = if (index == 1) Alignment.CenterStart else Alignment.Center // don't center name
    Box(modifier = Modifier.weight(colWeights[index]), contentAlignment = alignment) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = beerColumnValue,
            color = color
        )
    }
}

private val TapModel.info get() = listOf(
    tapNumber.toString(),
    name,
    price?.let { "$$it" } ?: "???",
    origin ?: "???",
    "${abv ?: 0}%"
)

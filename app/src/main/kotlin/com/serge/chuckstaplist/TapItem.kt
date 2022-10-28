package com.serge.chuckstaplist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.serge.chuckstaplist.api.TapModel

private const val FONT_SIZE_CHANGE_MULTIPLIER = .95f
private const val UNKNOWN_VALUE = "???"

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
internal fun LazyListScope.tapItem(
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
                    Text("Serving Size: ${tap.serving ?: UNKNOWN_VALUE}", textModifier, textAlign = TextAlign.Start)
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
) = tap.info.forEachIndexed { index, infoColumn ->
    val alignment = if (index == 1) Alignment.CenterStart else Alignment.Center // don't center name
    Box(modifier = Modifier.weight(colWeights[index]), contentAlignment = alignment) {
        var fontSizeMultiplier by remember(infoColumn.text) { mutableStateOf(1f) }
        Text(
            modifier = Modifier.padding(4.dp),
            text = infoColumn.text,
            maxLines = infoColumn.numLines,
            overflow = TextOverflow.Visible,
            style = LocalTextStyle.current.copy(fontSize = LocalTextStyle.current.fontSize * fontSizeMultiplier),
            color = color,
            onTextLayout = { if (it.hasVisualOverflow) fontSizeMultiplier *= FONT_SIZE_CHANGE_MULTIPLIER }
        )
    }
}

private val TapModel.info get() = listOf(
    TapInfoText(tapNumber.toString()),
    TapInfoText(name, numLines = 3),
    TapInfoText(price?.let { "$$it" } ?: UNKNOWN_VALUE),
    TapInfoText(origin ?: UNKNOWN_VALUE),
    TapInfoText("${abv ?: 0}%")
)

@Immutable
private data class TapInfoText(val text: String, val numLines: Int = 1)

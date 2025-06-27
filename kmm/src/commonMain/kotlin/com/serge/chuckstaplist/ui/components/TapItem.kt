package com.serge.chuckstaplist.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.api.markupPerOz
import com.serge.chuckstaplist.api.markupPerPour
import com.serge.chuckstaplist.api.showCrowler
import com.serge.chuckstaplist.api.showGrowler
import com.serge.chuckstaplist.ui.extensions.colorValue
import com.serge.chuckstaplist.ui.extensions.info
import com.serge.chuckstaplist.ui.extensions.servingSizeFormatted
import com.serge.chuckstaplist.ui.models.TapListColumns
import com.serge.chuckstaplist.ui.theme.Gray
import kotlin.math.roundToInt

private const val FONT_SIZE_CHANGE_MULTIPLIER = .95f

fun LazyListScope.tapItem(
    tap: TapModel,
    isExpanded: Boolean = false,
    bgColor: Color,
    borderColor: Color?,
    colWeights: TapListColumns.Weights,
    onClick: (TapModel) -> Unit,
    onLongClick: (TapModel) -> Unit = { }
) = item {
    Column(
        Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(2.dp)
            .border(2.dp, borderColor ?: Gray)
            .padding(2.dp)
            .combinedClickable(onLongClick = { onLongClick(tap) }) { onClick(tap) }
            .animateItem()
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
                    Text(
                        "Type: ${tap.type ?: "Other"}",
                        textModifier,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.End
                    )
                    Text("Serving Size: ${tap.servingSizeFormatted}",
                        textModifier,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Start
                    )
                }
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    if (tap.showGrowler) {
                        Text(
                            text = "Growler: ${tap.growlerCost.toMoneyString()}",
                            textModifier,
                            color = Color.LightGray,
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.End
                        )
                    }
                    if (tap.showCrowler) {
                        Text(
                            text = "Crowler: ${tap.crowlerCost.toMoneyString()}",
                            textModifier,
                            color = Color.LightGray,
                            style = MaterialTheme.typography.body2,
                            textAlign = TextAlign.Start
                        )
                    }
                }
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    Text(
                        "Markup / Pour: ${tap.markupPerPour.toMoneyString()}",
                        textModifier,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.End
                    )
                    Text(
                        "Markup / Oz: ${tap.markupPerOz.toMoneyString()}",
                        textModifier,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

private fun Double.toMoneyString(): String {
    val toInt = (this * 100).roundToInt()
    val zeroPad = if(toInt % 100 == 0 || toInt % 10 == 0) "0" else ""
    val roundedValue = (toInt / 100) + (toInt % 100).toFloat() / 100

    return "$${roundedValue}$zeroPad"
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
        val textStyle = MaterialTheme.typography.body1
        Text(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            text = infoColumn.text,
            maxLines = infoColumn.numLines,
            overflow = TextOverflow.Visible,
            style = textStyle.copy(fontSize = textStyle.fontSize * fontSizeMultiplier),
            color = color,
            onTextLayout = { if (it.hasVisualOverflow) fontSizeMultiplier *= FONT_SIZE_CHANGE_MULTIPLIER }
        )
    }
}

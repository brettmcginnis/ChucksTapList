import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.colorValue
import com.serge.chuckstaplist.ui.theme.DarkGray

@Composable
fun LazyTapList(
    list: List<TapModel>,
    state: LazyListState = rememberLazyListState(),
    expandedItems: Set<Int>,
    colWeights: FloatArray,
    onClick: (TapModel) -> Unit
) = LazyColumn(Modifier.fillMaxSize(), state) {
    list.forEachIndexed { index, beer ->
        val beerInfo = with(beer) {
            listOf(
                tapNumber.toString(),
                name,
                price?.let { "$$it" } ?: "???",
                origin ?: "???",
                "${abv ?: 0}%"
            )
        }

        val bgColor = if (index % 2 == 0) DarkGray else Color.Black
        tapItem(beer, beerInfo, expandedItems.contains(beer.tapNumber), bgColor, colWeights, onClick)
    }
}

@OptIn(ExperimentalAnimationApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
private fun LazyListScope.tapItem(
    tap: TapModel,
    beerInfo: List<String>,
    isExpanded: Boolean = false,
    bgColor: Color,
    colWeights: FloatArray,
    onClick: (TapModel) -> Unit
) = item {
    Column(
        Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(2.dp)
            .border(2.dp, Color.Gray)
            .padding(2.dp)
            .clickable { onClick(tap) }
            .animateItemPlacement()
    ) {
        Row(Modifier, Arrangement.Center, Alignment.CenterVertically) {
            BeerMainInfo(beerInfo, tap.colorValue, colWeights)
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
private fun RowScope.BeerMainInfo(
    beerInfo: List<String>,
    color: Color,
    colWeights: FloatArray
) = beerInfo.forEachIndexed { index, beerColumnValue ->
    val alignment = if (index == 1) Alignment.CenterStart else Alignment.Center // don't center name
    Box(modifier = Modifier.weight(colWeights[index]), contentAlignment = alignment) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = beerColumnValue,
            color = color
        )
    }
}
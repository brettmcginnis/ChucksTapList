package com.serge.chuckstaplist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.serge.chuckstaplist.ui.models.ColorFilterSet
import com.serge.chuckstaplist.ui.theme.Green
import com.serge.chuckstaplist.ui.theme.Orange
import com.serge.chuckstaplist.ui.theme.Pink
import com.serge.chuckstaplist.ui.theme.Red
import com.serge.chuckstaplist.ui.theme.Sky
import com.serge.chuckstaplist.ui.theme.Yellow

@Composable
fun ColorFilterHeader(
    colorFilterSet: ColorFilterSet,
    onFilterStateUpdated: (ColorFilterSet) -> Unit,
) = Column {
    val onFilterStateUpdatedState by rememberUpdatedState(onFilterStateUpdated)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(Orange, Yellow, Green, Pink, Sky, Red, White).forEach { color ->
            val colorSelected = colorFilterSet.contains(color)
            Box(
                Modifier
                    .fillMaxHeight()
                    .weight(1f, fill = false)
                    .padding(8.dp)
                    .aspectRatio(1f)
                    .run { background(color, RoundedCornerShape(if (colorSelected) 8.dp else 4.dp)) }
                    .clickable {
                        with(colorFilterSet) { if (colorSelected) minus(color) else plus(color) }
                            .run(::ColorFilterSet)
                            .run(onFilterStateUpdatedState)
                    }
            ) {
                if (colorSelected) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, "Checked", tint = MaterialTheme.colors.background)
                    }
                }
            }
        }
    }
}

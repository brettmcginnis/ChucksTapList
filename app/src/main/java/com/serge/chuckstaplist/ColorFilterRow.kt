package com.serge.chuckstaplist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.serge.chuckstaplist.ui.theme.Green
import com.serge.chuckstaplist.ui.theme.Orange
import com.serge.chuckstaplist.ui.theme.Pink
import com.serge.chuckstaplist.ui.theme.Red
import com.serge.chuckstaplist.ui.theme.Sky
import com.serge.chuckstaplist.ui.theme.Yellow

@Composable
fun ColorFilterRow(
    selectedFilters: Set<Color> = emptySet(),
    onFilterSelected: (Color) -> Unit
) = listOf(Orange, Yellow, Green, Pink, Sky, Red, White).forEach { color ->
    val onFilterCallbackRemembered by rememberUpdatedState(onFilterSelected)

    Box(
        Modifier
            .width(50.dp)
            .aspectRatio(1f)
            .padding(8.dp)
            .background(color)
            .clickable { onFilterCallbackRemembered(color) }
            .run { if (selectedFilters.contains(color)) border(4.dp, MaterialTheme.colors.background) else this }
    ) {
        if (selectedFilters.contains(color)) {
            Icon(
                Icons.Rounded.Check,
                "selected",
                Modifier.fillMaxSize(),
                MaterialTheme.colors.background
            )
        }
    }
}

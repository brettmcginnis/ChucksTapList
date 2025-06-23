package com.serge.chuckstaplist.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.serge.chuckstaplist.ChucksStore
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun getStorePainter(store: ChucksStore): Painter = painterResource(
    when (store) {
        ChucksStore.GREENWOOD -> "chucks_greenwood.jpg"
        ChucksStore.CENTRAL_DISTRICT -> "chucks_central_district.jpg"
        ChucksStore.SEWARD_PARK -> "chucks_seward_park.jpg"
    }
)
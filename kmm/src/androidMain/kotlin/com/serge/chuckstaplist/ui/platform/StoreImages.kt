package com.serge.chuckstaplist.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.serge.chuckstaplist.ChucksStore
import com.serge.chuckstaplist.kmm.R

@Composable
actual fun getStorePainter(store: ChucksStore): Painter = painterResource(
    id = when (store) {
        ChucksStore.GREENWOOD -> R.drawable.chucks_greenwood
        ChucksStore.CENTRAL_DISTRICT -> R.drawable.chucks_central_district
        ChucksStore.SEWARD_PARK -> R.drawable.chucks_seward_park
    }
)
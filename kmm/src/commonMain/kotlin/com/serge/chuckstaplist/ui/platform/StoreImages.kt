package com.serge.chuckstaplist.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import chuckstaplist.kmm.generated.resources.Res
import chuckstaplist.kmm.generated.resources.chucks_central_district
import chuckstaplist.kmm.generated.resources.chucks_greenwood
import chuckstaplist.kmm.generated.resources.chucks_seward_park
import com.serge.chuckstaplist.ChucksStore
import org.jetbrains.compose.resources.painterResource

@Composable
fun getStorePainter(store: ChucksStore): Painter = painterResource(
    when (store) {
        ChucksStore.GREENWOOD -> Res.drawable.chucks_greenwood
        ChucksStore.CENTRAL_DISTRICT -> Res.drawable.chucks_central_district
        ChucksStore.SEWARD_PARK -> Res.drawable.chucks_seward_park
    }
)

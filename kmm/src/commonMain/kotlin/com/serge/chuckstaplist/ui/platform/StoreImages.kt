package com.serge.chuckstaplist.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.serge.chuckstaplist.ChucksStore

@Composable
expect fun getStorePainter(store: ChucksStore): Painter
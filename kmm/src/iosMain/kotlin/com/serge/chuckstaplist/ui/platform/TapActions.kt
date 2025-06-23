package com.serge.chuckstaplist.ui.platform

import androidx.compose.runtime.Composable
import com.serge.chuckstaplist.api.TapModel

@Composable
actual fun TapLongClickHandler(tap: TapModel, onLongClick: () -> Unit): () -> Unit {
    return onLongClick
}
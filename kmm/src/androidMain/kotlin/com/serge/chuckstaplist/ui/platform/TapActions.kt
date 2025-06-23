package com.serge.chuckstaplist.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.serge.chuckstaplist.api.TapModel

@Composable
actual fun TapLongClickHandler(tap: TapModel, onLongClick: () -> Unit): () -> Unit {
    val context = LocalContext.current
    return {
        // For now, just call onLongClick. The openUntappdSearch function is in the app module
        // and would need to be migrated or accessed differently
        onLongClick()
    }
}
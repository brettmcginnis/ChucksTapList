package com.serge.chuckstaplist.compose.effects

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.launch

@Composable
fun ScrollToEffect(state: LazyListState, itemIndex: Int, onScroll: suspend () -> Unit) {
    val rememberOnScroll by rememberUpdatedState(onScroll)
    LaunchedEffect(itemIndex) {
        if (itemIndex < 0) return@LaunchedEffect
        launch { state.animateScrollToItem(itemIndex, -(state.layoutInfo.viewportSize.height / 2)) }
        rememberOnScroll()
    }
}

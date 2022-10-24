package com.serge.chuckstaplist.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun Lifecycle.whileResumed(block: suspend CoroutineScope.() -> Unit) {
    DisposableEffect(this) {
        var job: Job? = null
        val observer = LifecycleEventObserver { _, event ->
            job = if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch(block = block)
            } else {
                job?.cancel()
                null
            }
        }
        addObserver(observer)
        onDispose { removeObserver(observer) }
    }
}

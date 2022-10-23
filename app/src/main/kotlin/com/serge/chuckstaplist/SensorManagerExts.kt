package com.serge.chuckstaplist

import android.hardware.SensorManager
import android.os.SystemClock
import com.squareup.seismic.ShakeDetector
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

fun SensorManager.shakesFlow(delayAfterEventMs: Long = 0) = callbackFlow {
    with(ShakeDetector { trySendBlocking(SystemClock.elapsedRealtime()) }) {
        start(this@shakesFlow, SensorManager.SENSOR_DELAY_UI)
        awaitClose { stop() }
    }
}.buffer(Channel.UNLIMITED).distinctUntilChanged { old, new -> new - old < delayAfterEventMs }

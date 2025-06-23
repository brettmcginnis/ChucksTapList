package com.serge.chuckstaplist.platform

import android.content.Context
import android.hardware.SensorManager
import android.os.SystemClock
import com.squareup.seismic.ShakeDetector as SeismicShakeDetector
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

actual class ShakeDetector(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var detector: SeismicShakeDetector? = null
    
    actual fun shakesFlow(delayAfterEventMs: Long): Flow<Long> = callbackFlow {
        detector = SeismicShakeDetector { trySendBlocking(SystemClock.elapsedRealtime()) }
        detector?.start(sensorManager, SensorManager.SENSOR_DELAY_UI)
        awaitClose { detector?.stop() }
    }.buffer(Channel.UNLIMITED).distinctUntilChanged { old, new -> new - old < delayAfterEventMs }
    
    actual fun start() {
        // Implementation handled in shakesFlow
    }
    
    actual fun stop() {
        detector?.stop()
    }
}

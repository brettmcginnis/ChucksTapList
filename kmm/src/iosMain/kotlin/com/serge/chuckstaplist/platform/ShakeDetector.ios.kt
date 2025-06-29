package com.serge.chuckstaplist.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.CoreMotion.CMAccelerometerData
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.time.TimeSource

private const val SHAKE_THRESHOLD = 1.5
private const val SHAKE_SLOP_TIME_MS = 500L
private const val SHAKE_COUNT_RESET_MS = 3000L
private const val MIN_SHAKE_COUNT = 2

@OptIn(ExperimentalForeignApi::class)
actual class ShakeDetector {
    private val motionManager = CMMotionManager()
    private val operationQueue = NSOperationQueue()
    
    actual fun shakesFlow(delayAfterEventMs: Long): Flow<Long> = callbackFlow {
        if (!motionManager.accelerometerAvailable) {
            awaitClose { }
            return@callbackFlow
        }

        // State tracking
        var lastShakeTime = 0L
        var shakeCount = 0
        var lastAcceleration = 0.0
        val startTime = TimeSource.Monotonic.markNow()
        
        fun isShakeDetected(acceleration: Double, currentTime: Long): Boolean {
            val accelerationDelta = abs(acceleration - lastAcceleration)
            
            if (accelerationDelta > SHAKE_THRESHOLD) {
                val timeSinceLastShake = currentTime - lastShakeTime
                
                if (timeSinceLastShake > SHAKE_SLOP_TIME_MS) {
                    // Reset shake count if too much time has passed
                    if (timeSinceLastShake > SHAKE_COUNT_RESET_MS) {
                        shakeCount = 0
                    }
                    
                    shakeCount++
                    lastShakeTime = currentTime
                    
                    if (shakeCount >= MIN_SHAKE_COUNT) {
                        shakeCount = 0 // Reset for next shake sequence
                        return true
                    }
                }
            }
            
            return false
        }
        
        motionManager.accelerometerUpdateInterval = 0.05 // 50ms updates
        
        motionManager.startAccelerometerUpdatesToQueue(
            queue = operationQueue,
            withHandler = { data: CMAccelerometerData?, error ->
                if (error != null || data == null) return@startAccelerometerUpdatesToQueue
                
                val currentTime = startTime.elapsedNow().inWholeMilliseconds
                val acceleration = calculateAcceleration(data)
                
                if (isShakeDetected(acceleration, currentTime)) {
                    trySendBlocking(currentTime)
                }
                
                lastAcceleration = acceleration
            }
        )
        
        awaitClose { 
            motionManager.stopAccelerometerUpdates()
        }
    }.buffer(Channel.UNLIMITED).distinctUntilChanged { old, new -> new - old < delayAfterEventMs }
    
    private fun calculateAcceleration(data: CMAccelerometerData): Double {
        return data.acceleration.useContents {
            val x = this.x
            val y = this.y
            val z = this.z
            sqrt(x * x + y * y + z * z)
        }
    }
    
}

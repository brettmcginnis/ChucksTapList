package com.serge.chuckstaplist.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

actual class ShakeDetector {
    actual fun shakesFlow(delayAfterEventMs: Long): Flow<Long> = emptyFlow()
    
    actual fun start() {
        // No-op for iOS
    }
    
    actual fun stop() {
        // No-op for iOS
    }
}
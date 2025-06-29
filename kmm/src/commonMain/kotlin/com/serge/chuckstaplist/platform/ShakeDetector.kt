package com.serge.chuckstaplist.platform

import kotlinx.coroutines.flow.Flow

expect class ShakeDetector {
    fun shakesFlow(delayAfterEventMs: Long = 0): Flow<Long>
}
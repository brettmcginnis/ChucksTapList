package com.serge.chuckstaplist.domain

import kotlinx.datetime.LocalDateTime

data class FoodTruckEvent(
    val name: String,
    val url: String,
    val date: LocalDateTime,
)

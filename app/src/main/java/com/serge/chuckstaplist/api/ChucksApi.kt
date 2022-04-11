package com.serge.chuckstaplist.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ChucksApi {
    @GET("/data")
    suspend fun getTapList(@Query("menu") store: String = "GW"): List<TapModel>
}

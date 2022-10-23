package com.serge.chuckstaplist.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val BASE_URL = "https://taplists.web.app"

class ChucksApi(private val client: HttpClient) {
    suspend fun getTapList(store: String = "GW"): List<TapModel> = client.get("$BASE_URL/data?menu=$store").body()
}

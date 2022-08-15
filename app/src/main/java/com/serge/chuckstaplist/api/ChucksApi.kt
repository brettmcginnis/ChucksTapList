package com.serge.chuckstaplist.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.koin.core.annotation.Factory

private const val BASE_URL = "https://taplists.web.app"

@Factory
class ChucksApi(private val client: HttpClient) {
    suspend fun getTapList(store: String = "GW"): List<TapModel> = client.get("$BASE_URL/data?menu=$store")
}

package com.serge.chuckstaplist.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

expect fun createHttpEngine(): HttpClientEngine

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            isLenient = true
            allowSpecialFloatingPointValues = true
        }
    }

    single {
        HttpClient(createHttpEngine()) {
            expectSuccess = true
            install(ContentNegotiation) { json(get()) }
            install(DefaultRequest) { header(HttpHeaders.ContentType, ContentType.Application.Json) }
        }
    }
}

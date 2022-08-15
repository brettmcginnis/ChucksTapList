package com.serge.chuckstaplist.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            isLenient = true
        }
    }

    single {
        HttpClient(CIO) {
            install(JsonFeature) { serializer = KotlinxSerializer(get()) }
            install(DefaultRequest) { header(HttpHeaders.ContentType, ContentType.Application.Json) }
        }
    }
}

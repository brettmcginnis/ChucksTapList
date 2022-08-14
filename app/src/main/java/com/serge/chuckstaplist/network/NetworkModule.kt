package com.serge.chuckstaplist.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit

@OptIn(ExperimentalSerializationApi::class)
val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            isLenient = true
        }
    }

    single { OkHttpClient.Builder().build() }

    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl("https://taplists.web.app/")
            .addConverterFactory(get<Json>().asConverterFactory(MediaType.get("application/json")))
            .build()
    }
}

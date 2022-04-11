package com.serge.chuckstaplist.api

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TapModel(
    @SerialName("tap") val tapNumber: Int,
    @SerialName("beer") val name: String,
    @SerialName("Size") val size: String? = null,
    @SerialName("abv") val abv: String? = null,
    @SerialName("color") val color: String? = null,
    @SerialName("costOz") val costOz: Double = -1.0,
    @SerialName("crowler") val crowlerCost: Double = -1.0,
    @SerialName("growler") val growlerCost: Double = -1.0,
    @SerialName("origin") val origin: String? = null,
    @SerialName("oz") val oz: Int = -1,
    @SerialName("price") val price: String? = null,
    @SerialName("priceOz") val priceOz: Double = -1.0,
    @SerialName("serving") val serving: String? = null,
    @SerialName("shop") val shop: String? = null,
    @SerialName("type") val type: String? = null,
)

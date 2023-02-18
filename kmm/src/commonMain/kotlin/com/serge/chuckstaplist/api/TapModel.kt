package com.serge.chuckstaplist.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("price") internal val fullPrice: String? = null,
    @SerialName("half") internal val halfPrice: String? = null,
    @SerialName("quarter") internal val quarterPrice: String? = null,
    @SerialName("priceOz") val priceOz: Double = -1.0,
    @SerialName("Serving") internal val servingSize: String? = null,
    @SerialName("shop") val shop: String? = null,
    @SerialName("type") val type: String? = null,
)

val TapModel.serving
    get() = when {
        servingSize != null -> servingSize
        fullPrice.isNonZeroPrice -> "16 oz"
        halfPrice.isNonZeroPrice -> "8 oz"
        quarterPrice.isNonZeroPrice -> "4 oz"
        else -> "???"
    }

val TapModel.price
    get() = fullPrice.takeIf(String?::isNonZeroPrice)
        ?: halfPrice.takeIf(String?::isNonZeroPrice)
        ?: quarterPrice.takeIf(String?::isNonZeroPrice)

private val String?.isNonZeroPrice get() = (this?.toFloatOrNull() ?: 0f) > 0f

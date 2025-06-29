package com.serge.chuckstaplist.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class TapModel(
    @SerialName("tap") val tapNumber: Int,
    @SerialName("beer") val name: String,
    @SerialName("Size") val size: String? = null,
    @SerialName("abv") val abv: String? = null,
    @SerialName("color") val color: String? = null,
    @SerialName("costOz") val costOz: Double = -1.0,
    @SerialName("crowler") @JsonNames("Crowler") val crowlerCost: Double = -1.0,
    @SerialName("growler") @JsonNames("Growler") val growlerCost: Double = -1.0,
    @SerialName("origin") val origin: String? = null,
    @SerialName("oz") val oz: Int = -1,
    @SerialName("Full") internal val fullPrice: String? = null,
    @SerialName("Half") internal val halfPrice: String? = null,
    @SerialName("Quarter") internal val quarterPrice: String? = null,
    @SerialName("priceOz") val priceOz: Double = -1.0,
    @SerialName("serving") @JsonNames("Serving") internal val servingSize: String? = null,
    @SerialName("shop") val shop: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("No16") internal val no16oz: Boolean = false,
    @SerialName("No8") internal val no8oz: Boolean = false,
    @SerialName("NoGr") internal val noGrowler: Boolean = false,
)

val TapModel.serving
    get() = when {
        fullPrice.isNonZeroPrice && !no16oz -> 16
        halfPrice.isNonZeroPrice && !no8oz -> 8
        quarterPrice.isNonZeroPrice -> 4
        else -> 0
    }

val TapModel.price
    get() = fullPrice.takeIf(String?::isNonZeroPrice).takeUnless { no16oz }
        ?: halfPrice.takeIf(String?::isNonZeroPrice).takeUnless { no8oz }
        ?: quarterPrice.takeIf(String?::isNonZeroPrice)

val TapModel.showGrowler
    get() = growlerCost > 0 && !noGrowler

val TapModel.showCrowler
    get() = crowlerCost > 0 && !noGrowler

val TapModel.markupPerOz
    get() = (priceOz - costOz).coerceAtLeast(0.0)

val TapModel.markupPerPour
    get() = markupPerOz * serving

private val String?.isNonZeroPrice get() = (this?.toFloatOrNull() ?: 0f) > 0f

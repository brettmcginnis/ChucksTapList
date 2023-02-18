package com.serge.chuckstaplist

import android.os.Parcelable
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.api.price
import kotlinx.parcelize.Parcelize
import kotlin.Comparator

@Parcelize
data class TapListSortState(
    val columnIndex: Int = -1,
    val isAscending: Boolean = true,
    val type: Type = Type.TAP,
) : Comparator<TapModel>, Parcelable {

    enum class Type { TAP, NAME, PRICE, ORIGIN, ABV, COLOR }

    override fun compare(o1: TapModel, o2: TapModel): Int = when (type) {
        Type.TAP -> o1.tapNumber.compareTo(o2.tapNumber)
        Type.NAME -> o1.name.compareTo(o2.name)
        Type.PRICE -> (o1.price?.toDouble() ?: 0.0).compareTo(o2.price?.toDouble() ?: 0.0)
        Type.ORIGIN -> o1.origin.compareTo(o2.origin)
        Type.ABV -> (o1.abv?.toDouble() ?: 0.0).compareTo(o2.abv?.toDouble() ?: 0.0)
        Type.COLOR -> o1.color.compareTo(o2.color)
    }.let { if (isAscending) it else it * -1 }
}

private fun String?.compareTo(other: String?): Int {
    return this?.compareTo(other ?: return -1) ?: 1
}

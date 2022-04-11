package com.serge.chuckstaplist

import com.serge.chuckstaplist.api.TapModel
import java.util.Objects
import kotlin.Comparator

data class TapListSortState(
    val columnIndex: Int = -1,
    val isAscending: Boolean = true,
    val type: Type = Type.TAP,
) : Comparator<TapModel> {

    enum class Type { TAP, NAME, PRICE, ORIGIN, ABV, COLOR }

    override fun compare(o1: TapModel, o2: TapModel): Int = when (type) {
        Type.TAP -> Objects.compare(o1.tapNumber, o2.tapNumber, Int::compareTo)
        Type.NAME -> Objects.compare(o1.name, o2.name, String::compareTo)
        Type.PRICE -> Objects.compare(o1.price?.toDouble() ?: 0.0, o2.price?.toDouble() ?: 0.0, Double::compareTo)
        Type.ORIGIN -> Objects.compare(o1.origin, o2.origin, ::nullableStringComparator)
        Type.ABV -> Objects.compare(o1.abv?.toDouble() ?: 0.0, o2.abv?.toDouble() ?: 0.0, Double::compareTo)
        Type.COLOR -> Objects.compare(o1.color, o2.color, ::nullableStringComparator)
    }.let { if (isAscending) it else it * -1 }
}

private fun nullableStringComparator(s1: String?, s2: String?): Int {
    return s1?.compareTo(s2 ?: return -1) ?: 1
}

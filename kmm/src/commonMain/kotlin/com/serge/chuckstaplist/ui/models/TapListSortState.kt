package com.serge.chuckstaplist.ui.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import com.serge.chuckstaplist.api.TapModel
import com.serge.chuckstaplist.api.price

data class TapListSortState(
    val columnIndex: Int = -1,
    val isAscending: Boolean = true,
    val type: Type = Type.TAP,
) : Comparator<TapModel> {

    enum class Type { TAP, NAME, PRICE, ORIGIN, ABV, COLOR }

    override fun compare(a: TapModel, b: TapModel): Int = when (type) {
        Type.TAP -> a.tapNumber.compareTo(b.tapNumber)
        Type.NAME -> a.name.compareTo(b.name)
        Type.PRICE -> (a.price?.toDouble() ?: 0.0).compareTo(b.price?.toDouble() ?: 0.0)
        Type.ORIGIN -> a.origin.compareTo(b.origin)
        Type.ABV -> (a.abv?.toDouble() ?: 0.0).compareTo(b.abv?.toDouble() ?: 0.0)
        Type.COLOR -> a.color.compareTo(b.color)
    }.let { if (isAscending) it else it * -1 }

    companion object {
        val Saver = Saver<MutableState<TapListSortState>, List<Any>>(
            save = { state ->  with(state.value) { listOf(columnIndex, isAscending, type.ordinal) } },
            restore = { data -> 
                mutableStateOf(
                    TapListSortState(
                        columnIndex = data[0] as Int,
                        isAscending = data[1] as Boolean,
                        type = Type.entries[data[2] as Int]
                    )
                )
            }
        )
    }
}

private fun String?.compareTo(other: String?): Int {
    return this?.compareTo(other ?: return -1) ?: 1
}

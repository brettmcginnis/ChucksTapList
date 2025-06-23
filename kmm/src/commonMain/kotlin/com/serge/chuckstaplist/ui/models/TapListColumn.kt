package com.serge.chuckstaplist.ui.models

data class TapListColumn(
    val index: Int,
    val name: String,
    val weight: Float,
    val sortType: TapListSortState.Type
)
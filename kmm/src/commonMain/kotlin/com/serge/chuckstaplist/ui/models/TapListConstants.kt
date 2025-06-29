package com.serge.chuckstaplist.ui.models

val TAP_LIST_COLUMNS = listOf(
    TapListColumn(0, "#", .5f, TapListSortState.Type.TAP),
    TapListColumn(1, "Beer", 3f, TapListSortState.Type.NAME),
    TapListColumn(2, "Price", 1.2f, TapListSortState.Type.PRICE),
    TapListColumn(3, "Origin", .9f, TapListSortState.Type.ORIGIN),
    TapListColumn(4, "ABV%", 1f, TapListSortState.Type.ABV),
).run(::TapListColumns)
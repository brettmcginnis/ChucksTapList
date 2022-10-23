package com.serge.chuckstaplist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serge.chuckstaplist.ui.theme.DarkGreen

@Composable
fun TapListHeader(
    columns: TapListColumns,
    sortState: TapListSortState,
    onColumnClicked: (TapListColumn) -> Unit
) = Row(
    Modifier
        .background(DarkGreen)
        .padding(horizontal = 4.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    columns.sortedBy { it.index }.forEach { column ->
        Box(
            modifier = Modifier
                .weight(column.weight)
                .clickable { onColumnClicked(column) },
            contentAlignment = Alignment.Center
        ) {
            val nameWithSort = if (column.index == sortState.columnIndex) {
                "${column.name} ${if (sortState.isAscending) "▼" else "▲"}"
            } else column.name
            Text(nameWithSort, Modifier.padding(vertical = 4.dp), fontSize = 12.sp)
        }
    }
}

@Immutable
class TapListColumns(columns: List<TapListColumn>) : List<TapListColumn> by columns {
    @Immutable class Weights(weights: List<Float>) : List<Float> by weights

    val weights by lazy { Weights(map { it.weight }) }
}

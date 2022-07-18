package com.serge.chuckstaplist.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.serge.chuckstaplist.FoodTruckList
import com.serge.chuckstaplist.ui.theme.Orange

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoodTruckRow(
    foodTrucks: FoodTruckList,
    onTruckSelected: (FoodTruckEvent) -> Unit,
) = LazyRow(Modifier.fillMaxWidth().height(44.dp)) {
    foodTrucks.forEach { foodTruck ->
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(2.dp)
                    .border(2.dp, Orange)
                    .combinedClickable { onTruckSelected(foodTruck) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(6.dp),
                    text = foodTruck.name
                )
            }
        }
    }
}

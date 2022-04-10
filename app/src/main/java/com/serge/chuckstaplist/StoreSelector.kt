package com.serge.chuckstaplist

import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.serge.chuckstaplist.ui.theme.ChucksTapListTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StoreSelector(
    selectedStore: ChucksStore,
    onStoreSelected: (ChucksStore) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            readOnly = true,
            value = selectedStore.name,
            onValueChange = {},
            label = { Text("Store", color = MaterialTheme.colors.secondary) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ChucksStore.values().forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        onStoreSelected(selectionOption)
                        expanded = false
                    }
                ) {
                    Text(text = selectionOption.name)
                }
            }
        }
    }
}

@Preview
@Composable
fun StoreSelectorPreview() {
    ChucksTapListTheme {
        StoreSelector(ChucksStore.GREENWOOD) {}
    }
}
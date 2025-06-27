package com.serge.chuckstaplist.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serge.chuckstaplist.ChucksStore
import com.serge.chuckstaplist.ui.platform.getStorePainter
import com.serge.chuckstaplist.ui.platform.isLandscape
import com.serge.chuckstaplist.ui.theme.DarkGreen

private const val STORE_NAME_VERTICAL_BIAS = -.5f
private const val STORE_NAME_BACKGROUND_ALPHA = .9f

@Composable
fun StoreSelector(onStoreSelected: (ChucksStore) -> Unit) {
    val stores = listOf(ChucksStore.SEWARD_PARK, ChucksStore.GREENWOOD, ChucksStore.CENTRAL_DISTRICT)
    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Text(
            "Chuck's Hop Shop",
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        if (isLandscape) {
            Row(Modifier.fillMaxHeight()) {
                stores.forEach { store ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp, 8.dp)
                            .clickable { onStoreSelected(store) }
                    ) { addStore(store) }
                }
            }
        } else {
            stores.forEach { store ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp, 4.dp)
                        .clickable { onStoreSelected(store) }
                ) { addStore(store) }
            }
        }
    }
}

@Composable
private fun BoxScope.addStore(store: ChucksStore) {
    Image(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp)),
        painter = getStorePainter(store),
        contentScale = ContentScale.Crop,
        contentDescription = store.storeName
    )
    Text(
        store.storeName,
        modifier = Modifier
            .align(BiasAlignment(0f, STORE_NAME_VERTICAL_BIAS))
            .fillMaxWidth()
            .background(DarkGreen.copy(alpha = STORE_NAME_BACKGROUND_ALPHA), RectangleShape)
            .padding(vertical = 8.dp),
        color = Color.White,
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

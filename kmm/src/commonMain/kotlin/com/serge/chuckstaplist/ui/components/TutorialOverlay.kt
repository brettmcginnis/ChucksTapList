package com.serge.chuckstaplist.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serge.chuckstaplist.ui.theme.DarkGray
import com.serge.chuckstaplist.ui.theme.DarkGreen
import com.serge.chuckstaplist.ui.theme.Gray
import com.serge.chuckstaplist.ui.theme.Green
import com.serge.chuckstaplist.ui.theme.Orange
import com.serge.chuckstaplist.ui.theme.Pink
import com.serge.chuckstaplist.ui.theme.Sky
import com.serge.chuckstaplist.ui.theme.Yellow

@Composable
fun TutorialOverlay(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(interactionSource = null, indication = null) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(initialScale = 0.8f),
                exit = scaleOut(targetScale = 0.8f),
                modifier = Modifier
            ) {
                TutorialCard(
                    onDismiss = onDismiss,
                    modifier = Modifier
                        .widthIn(min = 280.dp, max = 480.dp)
                        .fillMaxWidth(0.9f)
                        .clickable(enabled = false) { } // Prevent clicks from propagating
                )
            }
        }
    }
}

@Composable
private fun TutorialCard(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .heightIn(max = 600.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkGray)
            .border(2.dp, Gray, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "How to Use Chuck's Tap List",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.h6,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))
        
        // Content that scrolls only when needed
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tutorial tips
            TutorialTip(
                icon = Icons.Default.ColorLens,
                text = "Tap the color blocks at the top to filter by your favorite beer styles",
                iconColor = Orange
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TutorialTip(
                icon = Icons.AutoMirrored.Filled.Sort,
                text = "Tap column headers to sort by tap, name, price or ABV",
                iconColor = Green
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TutorialTip(
                icon = Icons.Default.Info,
                text = "Tap any beer to see detailed information including growler prices",
                iconColor = Yellow
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TutorialTip(
                icon = Icons.Default.Search,
                text = "Long-press any beer to search for it on Untappd",
                iconColor = Sky
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TutorialTip(
                icon = Icons.Default.Vibration,
                text = "Shake your device to highlight a random beer and discover something new",
                iconColor = Pink
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Dismiss button
        Text(
            text = "Got it!",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DarkGreen)
                .clickable { onDismiss() }
                .padding(12.dp),
            color = Color.White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.button,
        )
    }
}

@Composable
private fun TutorialTip(
    icon: ImageVector,
    text: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.weight(1f)
        )
    }
}

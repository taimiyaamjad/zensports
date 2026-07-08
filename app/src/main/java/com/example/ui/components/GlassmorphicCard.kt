package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    cardStyle: String = "apollo", // "apollo" (glass), "flat" (solid)
    content: @Composable BoxScope.() -> Unit
) {
    if (cardStyle == "apollo") {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x22FFFFFF), // Semi-transparent white
                            Color(0x08FFFFFF)  // Very transparent white
                        )
                    )
                )
                .border(
                    BorderStroke(
                        borderWidth,
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x33FFFFFF), // Brighter white outline at top
                                Color(0x0DFFFFFF)  // Faded outline at bottom
                            )
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
        ) {
            content()
        }
    } else {
        // Flat M3 surface
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(cornerRadius),
            color = Color(0xFF1E212E),
            border = BorderStroke(1.dp, Color(0xFF2C3145))
        ) {
            Box {
                content()
            }
        }
    }
}

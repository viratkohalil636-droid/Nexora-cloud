package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CosmicBackgroundDark

@Composable
fun CosmicBackground(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CosmicBackgroundDark)
      .drawBehind {
        // Deep cosmic nebula glows
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Top right cyan nebula glow
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x1A00F0FF), Color.Transparent),
            center = Offset(canvasWidth * 0.85f, canvasHeight * 0.12f),
            radius = canvasWidth * 0.65f
          )
        )

        // Middle left violet nebula glow
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x188B5CF6), Color.Transparent),
            center = Offset(canvasWidth * 0.15f, canvasHeight * 0.45f),
            radius = canvasWidth * 0.75f
          )
        )

        // Bottom right cosmic indigo glow
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x146366F1), Color.Transparent),
            center = Offset(canvasWidth * 0.75f, canvasHeight * 0.85f),
            radius = canvasWidth * 0.7f
          )
        )
      }
  ) {
    content()
  }
}

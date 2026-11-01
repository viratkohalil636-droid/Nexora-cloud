package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CyanNeon,
    onPrimary = Color(0xFF03141F),
    primaryContainer = Color(0xFF083344),
    onPrimaryContainer = Color(0xFFCFFAFE),
    secondary = VioletNeon,
    onSecondary = Color(0xFF1E0A40),
    secondaryContainer = Color(0xFF3B1B75),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = IndigoNeon,
    onTertiary = Color.White,
    background = CosmicBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = CosmicSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CosmicSurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    error = RoseDanger,
    onError = Color.White,
    outline = BorderGlowDark,
    outlineVariant = BorderSubtleDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = SecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary = TertiaryLight,
    onTertiary = Color.White,
    background = CosmicBackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = CosmicSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = CosmicSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569),
    error = RoseDanger,
    onError = Color.White,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to futuristic cosmic dark theme for Nexora Cloud
  dynamicColor: Boolean = false, // Preserve distinctive brand nebula look
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

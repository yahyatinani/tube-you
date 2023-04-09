package com.github.whyrising.vancetube.modules.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorPalette = darkColorScheme(
  primary = Purple200,
  primaryContainer = Color.Black,
  secondary = Teal200,
  surface = Grey900,
  onSurface = Color.White,
  onSurfaceVariant = Color.White,
  background = Grey900,
  surfaceVariant = Color.Cyan,
  onBackground = Color.White
)

val LightColorPalette = lightColorScheme(
  primary = Purple500,
  primaryContainer = Color.White,
  secondary = Teal200,
  surface = Color.White,
  background = Color.White,
  onBackground = Color.Black
)

@Composable
fun isCompact(windowSizeClass: WindowSizeClass) =
  windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

@Composable
fun VanceTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  isCompact: Boolean = true,
  content: @Composable () -> Unit
) {
  val typography = when {
    isCompact -> TypographyCompact
    else -> TypographyExpanded
  }

  MaterialTheme(
    colorScheme = if (darkTheme) DarkColorPalette else LightColorPalette,
    typography = typography,
    shapes = Shapes,
    content = content
  )
}

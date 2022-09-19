@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package com.github.whyrising.vancetube.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize.Companion.Zero

private val DarkColorPalette = darkColorScheme(
  primary = Purple200,
  primaryContainer = Purple700,
  secondary = Teal200
)

private val LightColorPalette = lightColorScheme(
  primary = Purple500,
  primaryContainer = Purple700,
  secondary = Teal200

  /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun isCompact(windowSizeClass: WindowSizeClass) =
  windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

@Composable
fun VanceTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  windowSizeClass: WindowSizeClass = WindowSizeClass.calculateFromSize(Zero),
  content: @Composable () -> Unit
) {
  val typography = when {
    isCompact(windowSizeClass) -> TypographyCompact
    else -> TypographyExpanded
  }

  MaterialTheme(
    colorScheme = if (darkTheme) DarkColorPalette else LightColorPalette,
    typography = typography,
    shapes = Shapes,
    content = content
  )
}

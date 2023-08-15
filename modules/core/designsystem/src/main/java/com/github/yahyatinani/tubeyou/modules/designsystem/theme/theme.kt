package com.github.yahyatinani.tubeyou.modules.designsystem.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
  primary = Color.White,
  primaryContainer = primaryContainerDark,
  onPrimaryContainer = Color.White,
  secondary = Teal200,
  surface = Grey900,
  onSurface = Color.White,
  onSurfaceVariant = Color.White,
  background = Grey900,
  surfaceVariant = Color.Cyan,
  onBackground = Color.White,
  surfaceTint = Grey900
)

val LightColorScheme = lightColorScheme(
  primary = Color.Black,
  primaryContainer = primaryContainerLight,
  onPrimaryContainer = Color.Black,
  secondary = Teal200,
  surface = Color.White,
  background = Color.White,
  onBackground = Color.Black,
  surfaceTint = Color.White
)

@Composable
fun isCompact(windowSizeClass: WindowSizeClass) =
  windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

@Composable
fun TyTheme(
  isDarkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  isCompact: Boolean = true,
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (isDarkTheme) {
        dynamicDarkColorScheme(context)
      } else {
        dynamicLightColorScheme(context)
      }
    }

    isDarkTheme -> DarkColorScheme
    else -> LightColorScheme
  }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.primary.toArgb()
      WindowCompat.getInsetsController(
        window,
        view
      ).isAppearanceLightStatusBars = isDarkTheme
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = if (isCompact) TypographyCompact else TypographyExpanded,
    shapes = Shapes
  ) {
    CompositionLocalProvider(
      LocalRippleTheme provides TyThemeRippleTheme,
      content = content
    )
  }
}

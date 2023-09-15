package com.github.yahyatinani.tubeyou.modules.designsystem.theme

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.View
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
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
  surface = Gray900,
  onSurface = Color.White,
  onSurfaceVariant = Color.White,
  background = Gray900,
  surfaceVariant = Color.Cyan,
  onBackground = Color.White,
  surfaceTint = Gray900,
  outlineVariant = Color.White.copy(.12f)
)

val LightColorScheme = lightColorScheme(
  primary = Color.Black,
  primaryContainer = primaryContainerLight,
  onPrimaryContainer = Color.Black,
  secondary = Teal200,
  surface = Color.White,
  background = Color.White,
  onBackground = Color.Black,
  surfaceTint = Color.White,
  outlineVariant = Color.Black.copy(.12f)
)

@Immutable
data class ExtendedColors(
  val popupContainer: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
  ExtendedColors(
    popupContainer = Color.Unspecified
  )
}

@Composable
fun isCompact(windowSizeClass: WindowSizeClass) = remember(windowSizeClass) {
  windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
    windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
}

@Composable
private fun WindowSideEffects(
  view: View,
  background: Color,
  isDarkTheme: Boolean
) {
  val bitmap = remember(background) {
    Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888).apply {
      eraseColor(background.toArgb())
    }
  }
  val bgToArgb = background.toArgb()
  val resources = LocalContext.current.resources
  SideEffect {
    val window = (view.context as Activity).window
    window.statusBarColor = bgToArgb
    window.navigationBarColor = bgToArgb
    WindowCompat.getInsetsController(
      window,
      view
    ).isAppearanceLightStatusBars = !isDarkTheme

    // Set to true so the bottom bar doesn't get pushed up by the keyboard.
    // But It causes the cutout area in landscape to be gray.
    WindowCompat.setDecorFitsSystemWindows(window, true)
    // Fix the cutout area color in landscape.
    window.setBackgroundDrawable(BitmapDrawable(resources, bitmap))
  }
}

@Composable
fun TyTheme(
  isDarkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
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
    WindowSideEffects(view, colorScheme.background, isDarkTheme)
  }

  val extendedColors = ExtendedColors(
    popupContainer = if (isDarkTheme) Color(0xFF212121) else Color.White
  )

  CompositionLocalProvider(
    LocalRippleTheme provides TyThemeRippleTheme,
    LocalExtendedColors provides extendedColors
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = if (isCompact) TypographyCompact else TypographyExpanded,
      shapes = Shapes,
      content = content
    )
  }
}

object TyTheme {
  val colors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current
}

package com.github.yahyatinani.tubeyou.modules.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object TyThemeRippleTheme : RippleTheme {
  // Here you should return the ripple color you want
  // and not use the defaultRippleColor extension on RippleTheme.
  // Using that will override the ripple color set in DarkMode
  // or when you set light parameter to false
  @Composable
  override fun defaultColor(): Color = LocalContentColor.current

  @Composable
  override fun rippleAlpha(): RippleAlpha {
    return RippleAlpha(
      pressedAlpha = when {
        isSystemInDarkTheme() -> containerAlphaDark
        else -> containerAlphaLight
      },
      hoveredAlpha = 0f,
      focusedAlpha = 0f,
      draggedAlpha = 0f
    )
  }
}

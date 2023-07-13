package com.github.yahyatinani.tubeyou.modules.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// TODO: support all windowSizeClass.

val TypographyCompact = Typography(
  labelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 9.sp
  ),
  titleMedium = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold
  )
)

val TypographyExpanded = Typography(
  labelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp
  ),
  titleMedium = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Bold
  )
  /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

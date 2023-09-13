package com.github.yahyatinani.tubeyou.modules.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun IconBorder(
  imageVector: ImageVector,
  colorScheme: ColorScheme,
  tint: Color
) {
  Box {
    Icon(
      modifier = Modifier
        .size(14.dp)
        .align(Alignment.Center),
      imageVector = imageVector,
      contentDescription = "",
      tint = colorScheme.surface
    )
    Icon(
      modifier = Modifier
        .size(12.dp)
        .align(Alignment.Center),
      imageVector = imageVector,
      contentDescription = "",
      tint = tint
    )
  }
}

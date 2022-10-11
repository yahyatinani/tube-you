package com.github.whyrising.vancetube.modules.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ThumbnailImage(modifier: Modifier = Modifier, url: String?) {
  AsyncImage(
    model = url,
    contentDescription = "thumbnail",
    modifier = modifier
      .background(Color.DarkGray)
      .layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val width = placeable.width
        val relativeHeightPx = (width * 720) / 1280
        layout(width, relativeHeightPx) {
          placeable.placeRelative(0, 0)
        }
      },
    contentScale = ContentScale.FillWidth
  )
}

@Composable
fun Thumbnail(
  modifier: Modifier,
  url: String?,
  videoLength: String
) {
  Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
    ThumbnailImage(
      modifier = modifier.fillMaxWidth(),
      url = url
    )
    VideoLengthText(
      modifier = Modifier.padding(8.dp),
      videoLength = videoLength
    )
  }
}

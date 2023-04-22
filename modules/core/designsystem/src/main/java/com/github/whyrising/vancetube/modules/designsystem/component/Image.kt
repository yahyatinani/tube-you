package com.github.whyrising.vancetube.modules.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

const val THUMBNAIL_HEIGHT = 720f
const val THUMBNAIL_WIDTH = 1280f

@Composable
fun screenWidthPx(
  density: Density = LocalDensity.current,
  configuration: Configuration = LocalConfiguration.current
): Float = remember(configuration, density) {
  with(density) { configuration.screenWidthDp.dp.toPx() }
}

@Composable
fun rememberThumbnailHeight(
  screenWidthPx: Float = screenWidthPx(),
  density: Density = LocalDensity.current
): Dp = remember(key1 = screenWidthPx, key2 = density) {
  val heightPx = screenWidthPx * THUMBNAIL_HEIGHT / THUMBNAIL_WIDTH
  with(density) { heightPx.toDp() }
}

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
  videoLength: @Composable () -> Unit = {}
) {
  Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
    ThumbnailImage(
      modifier = modifier.fillMaxWidth(),
      url = url
    )
    videoLength()
  }
}

private val avatarSize = 72.dp

@Composable
fun ChannelAvatar(modifier: Modifier = Modifier, url: String?) {
  AsyncImage(
    model = url,
    contentDescription = "channel's avatar",
    modifier = modifier
      .clip(CircleShape)
      .width(avatarSize)
      .height(avatarSize)
      .background(Color.DarkGray),
    contentScale = ContentScale.FillWidth
  )
}

@Preview(showBackground = true)
@Composable
fun ChannelAvatarDarkPreview() {
  ChannelAvatar(url = "")
}

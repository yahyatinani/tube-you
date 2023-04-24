package com.github.whyrising.vancetube.modules.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
fun rememberThumbnailHeightPortrait(
  screenWidthPx: Float = screenWidthPx(),
  density: Density = LocalDensity.current
): Dp = remember(key1 = screenWidthPx, key2 = density) {
  with(density) { (screenWidthPx * THUMBNAIL_HEIGHT / THUMBNAIL_WIDTH).toDp() }
}

@Composable
fun rememberThumbnailHeightLandscape(
  screenWidthPx: Float = screenWidthPx(),
  density: Density = LocalDensity.current
): Dp = remember(key1 = screenWidthPx, key2 = density) {
  with(density) {
    ((screenWidthPx / 4.6f) * THUMBNAIL_HEIGHT / THUMBNAIL_WIDTH).toDp()
  }
}

@Composable
fun Thumbnail(
  modifier: Modifier,
  url: String?,
  content: @Composable BoxScope.() -> Unit = {}
) {
  Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
    AsyncImage(
      model = url,
      contentDescription = "thumbnail",
      modifier = modifier
        .fillMaxWidth()
        .background(Color.DarkGray),
      contentScale = ContentScale.FillWidth
    )
    content()
  }
}

val LARGE_AVATAR = 72.dp
val MINI_AVATAR = 40.dp

@Composable
fun ChannelAvatar(url: String?, modifier: Modifier = Modifier) {
  AsyncImage(
    model = url,
    contentDescription = "channel's avatar",
    modifier = modifier
      .clip(CircleShape)
      .background(Color.DarkGray),
    contentScale = ContentScale.FillWidth
  )
}

// -- Previews -----------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun ChannelAvatarDarkPreview() {
  ChannelAvatar(url = "")
}

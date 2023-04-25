package com.github.whyrising.vancetube.modules.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.github.whyrising.vancetube.modules.designsystem.R

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
val MINI_AVATAR_LIVE = 44.dp

@Composable
fun ChannelAvatar(
  url: String?,
  modifier: Modifier = Modifier,
  size: Dp = MINI_AVATAR
) {
  AsyncImage(
    model = url,
    contentDescription = "channel's avatar",
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(Color.DarkGray),
    contentScale = ContentScale.FillWidth
  )
}

@Composable
fun ChannelAvatarLive(
  url: String?,
  modifier: Modifier = Modifier,
  size: Dp = MINI_AVATAR_LIVE
) {
  Box(
    modifier = Modifier,
    contentAlignment = Alignment.BottomCenter
  ) {
    AsyncImage(
      model = url,
      contentDescription = "live channel's avatar",
      modifier = modifier
        .size(size)
        .border(width = 2.dp, color = Red, shape = CircleShape)
        .padding(4.dp)
        .clip(CircleShape)
        .background(Color.DarkGray)
    )
    DurationText(
      text = stringResource(R.string.duration_live),
      modifier = Modifier
        .wrapContentSize()
        .offset(y = 1.5.dp)
        .border(
          width = 2.dp,
          color = MaterialTheme.colorScheme.surface,
          shape = roundedCornerShape
        )
        .padding(2.dp)
        .background(color = Red)
        .padding(horizontal = 2.dp),
      fontSize = 10.sp
    )
  }
}

// -- Previews -----------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun ChannelAvatarDarkPreview() {
  ChannelAvatar(url = "")
}

@Preview(showBackground = true)
@Composable
fun ChannelAvatarLiveDarkPreview() {
  ChannelAvatarLive(url = "")
}

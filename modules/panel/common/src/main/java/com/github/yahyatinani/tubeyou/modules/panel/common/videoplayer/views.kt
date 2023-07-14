package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ChannelAvatar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.SubscribeButton
import com.github.yahyatinani.tubeyou.modules.designsystem.component.Thumbnail
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Grey300
import com.github.yahyatinani.tubeyou.modules.panel.common.Stream
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.dispatchSync
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v

@Composable
fun QualityList(
  modifier: Modifier = Modifier,
  resolutions: List<Pair<String, Int>>,
  containerColor: Color
) {
  LazyColumn(modifier = modifier.padding(bottom = 16.dp)) {
    items(items = resolutions) { res ->
      ListItem(
        modifier = Modifier
          .clickable { dispatch(v("set_player_resolution", res.second)) }
          .padding(horizontal = 56.dp),
        headlineContent = {
          Text(text = res.first)
        },
        colors = ListItemDefaults.colors(containerColor = containerColor)
      )
    }
  }
}

@Composable
fun DragHandle(modifier: Modifier = Modifier) {
  Surface(
    modifier = modifier
      .padding(vertical = 8.dp)
      .semantics { contentDescription = "dragHandleDescription" },
    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f),
    shape = MaterialTheme.shapes.extraLarge
  ) {
    Box(Modifier.size(width = 32.0.dp, height = 4.0.dp))
  }
}

private fun Context.findWindow(): Window? {
  var context = this
  while (context is ContextWrapper) {
    if (context is Activity) return context.window
    context = context.baseContext
  }
  return null
}

@Composable
fun MiniPlayerControls(
  isPlaying: Boolean,
  onClosePlayer: () -> Unit = { },
  playPausePlayer: () -> Unit = { }
) {
  with(LocalContext.current.findWindow()) {
    this?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
  }
  Row(
    modifier = Modifier.height(110.dp - 48.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    val color = MaterialTheme.colorScheme.onBackground
    val colorFilter = ColorFilter.tint(color = color)
    IconButton(onClick = playPausePlayer) {
      val playerIcon = when (isPlaying) {
        true -> Icons.Default.Pause
        else -> Icons.Default.PlayArrow
      }
      Image(
        imageVector = playerIcon,
        contentDescription = "play/pause",
        colorFilter = colorFilter
      )
    }

    IconButton(onClick = onClosePlayer) {
      Image(
        imageVector = Icons.Default.Close,
        contentDescription = "close video",
        colorFilter = colorFilter
      )
    }
  }
}

@Composable
@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
fun VideoPlayer(
  modifier: Modifier = Modifier,
  streamData: IPersistentMap<Any, Any>?,
  useController: Boolean = true,
  isCollapsed: Boolean,
  playerState: PlayerState?,
  showThumbnail: Boolean?,
  thumbnail: String?
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val orientation = LocalConfiguration.current.orientation

    var showQualityControl: Boolean by remember { mutableStateOf(false) }
    var showQualitiesSheet: Boolean by remember { mutableStateOf(false) }

    val ratio by remember { mutableFloatStateOf(16 / 9f) }
    Box(
      modifier = Modifier
        .then(
          if (orientation != ORIENTATION_LANDSCAPE) {
            if (isCollapsed) {
              Modifier
                .height(110.dp - 48.dp)
                .aspectRatio(ratio)
            } else if (streamData != null) {
              Modifier
                .fillMaxWidth()
                .aspectRatio(get(streamData, Stream.aspect_ratio)!!)
            } else {
              Modifier
                .fillMaxWidth()
                .aspectRatio(ratio)
            }
          } else {
            Modifier
          }
        )
    ) {
      LaunchedEffect(streamData) {
        TyPlayer.playNewVideo(streamData)
      }

      LaunchedEffect(orientation) {
        if (orientation == ORIENTATION_LANDSCAPE) {
          dispatch(v(":player_fullscreen_landscape"))
        } else {
          dispatch(v(":player_portrait"))
        }
      }

      AndroidView(
        modifier = modifier
          .fillMaxWidth()
          .apply {
            if (orientation == ORIENTATION_LANDSCAPE) {
              padding(start = 26.dp)
            }
          },
        factory = {
          regPlaybackFxs(scope)
          regPlaybackEvents()
          PlayerView(context).apply {
            setControllerVisibilityListener(
              PlayerView.ControllerVisibilityListener { visibility: Int ->
                showQualityControl = visibility == View.VISIBLE
              }
            )
            player = TyPlayer.getInstance()
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
          }
        }
      ) {
        // setBackgroundColor(ContextCompat.getColor(context, R.color.black))
        it.useController = useController
      }

      if (showThumbnail == true) {
        Thumbnail(
          modifier = Modifier.wrapContentSize(),
          url = thumbnail
        )
      }

      if (
        playerState == PlayerState.LOADING ||
        playerState == PlayerState.BUFFERING
      ) {
        CircularProgressIndicator(
          modifier = Modifier
            .align(Alignment.Center)
            .size(64.dp),
          color = Grey300.copy(alpha = .4f)
        )
      }

      if (showQualityControl && streamData != null) {
        TextButton(
          modifier = Modifier.align(Alignment.TopEnd),
          colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
          onClick = {
            dispatch(v("playback_fsm", "generate_quality_list"))
            showQualitiesSheet = true
          }
        ) {
          Text(text = get<String>(streamData, Stream.current_quality)!!)
        }

        IconButton(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 40.dp, bottom = 6.dp),
          onClick = { dispatchSync(v(":toggle_orientation")) }
        ) {
          Image(
            imageVector = Icons.Default.Fullscreen,
            contentDescription = "",
            colorFilter = ColorFilter.tint(
              MaterialTheme.colorScheme.onBackground
            )
          )
        }
      }
    }

    if (showQualitiesSheet) {
      val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
      LaunchedEffect(Unit) {
        val offset = sheetState.requireOffset()
        println("sjdfjsdfj $offset")
      }

      val containerColor = Color(0xFF212121)
      ModalBottomSheet(
        modifier = Modifier
//          .offset(y = (-24).dp)
          .padding(horizontal = 10.dp)
          .fillMaxWidth()
          .wrapContentHeight(),
        dragHandle = { DragHandle(modifier) },
        shape = RoundedCornerShape(10.dp),
        containerColor = containerColor,
        onDismissRequest = { showQualitiesSheet = false },
        sheetState = sheetState
      ) {
        val resolutions =
          get<List<Pair<String, Int>>>(streamData, Stream.quality_list)!!
        QualityList(
          resolutions = resolutions,
          containerColor = containerColor
        )
      }
    }

    if (isCollapsed) {
      MiniPlayerControls(
        isPlaying = playerState == PlayerState.PLAYING,
        onClosePlayer = {
          dispatchSync(v("playback_fsm", "close_player"))
        }
      ) {
        dispatchSync(v("playback_fsm", "toggle_play_pause"))
      }
    }
  }
}

@Composable
fun DescriptionSection(
  modifier: Modifier = Modifier,
  streamTitle: String,
  views: String,
  date: String
) {
  Column(
    modifier = modifier.padding(top = 12.dp, bottom = 2.dp)
  ) {
    val typography = MaterialTheme.typography
    Text(
      text = streamTitle,
      style = typography.titleMedium,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis
    )
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f)
    val string = buildAnnotatedString {
      withStyle(
        style = typography.bodySmall.copy(color = color).toSpanStyle()
      ) {
        append("$views  $date")
      }
      append(" ")
      withStyle(
        style = typography.bodySmall.copy(
          fontWeight = FontWeight.Bold
        ).toSpanStyle()
      ) {
        append("...more")
      }
    }
    Text(text = string)
  }
}

@Composable
fun ChannelSection(
  modifier: Modifier = Modifier,
  channelName: String,
  channelAvatar: String,
  subscribersCount: String
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    val typography = MaterialTheme.typography

    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ChannelAvatar(
        url = channelAvatar,
        size = 32.dp
      )

      Spacer(modifier = Modifier.width(16.dp))

      Text(
        modifier = Modifier.weight(1f, false),
        text = channelName,
        style = typography.bodyMedium.copy(fontWeight = FontWeight.W500),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
      )

      Spacer(modifier = Modifier.width(16.dp))

      val color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f)
      Text(
        text = subscribersCount,
        style = typography.bodySmall.copy(color = color)
      )
    }

    Spacer(modifier = Modifier.width(16.dp))

    val colorScheme = MaterialTheme.colorScheme

    SubscribeButton(
      containerColor = colorScheme.onSurface,
      contentColor = colorScheme.surface
    ) {
      // TODO:
    }
  }
}

@Composable
fun PlaybackBottomSheet(
  isCollapsed: Boolean,
  onCollapsedClick: () -> Unit,
  streamData: IPersistentMap<Any, Any>?,
  playerRegion: PlayerState?,
  showThumbnail: Boolean?,
  thumbnail: String?
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .clickable(isCollapsed, onClick = onCollapsedClick)
  ) {
    VideoPlayer(
      streamData = streamData,
      useController = !isCollapsed,
      isCollapsed = isCollapsed,
      playerState = playerRegion,
      showThumbnail = showThumbnail,
      thumbnail = thumbnail
    )

    if (streamData == null) return@Column

    DescriptionSection(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { }
        .padding(horizontal = 12.dp),
      streamTitle = get<String>(streamData, Stream.title)!!,
      views = get<String>(streamData, Stream.views)!!,
      date = get<String>(streamData, Stream.date)!!
    )

    ChannelSection(
      modifier = Modifier
        .clickable { }
        .padding(horizontal = 12.dp),
      channelName = get<String>(streamData, Stream.channel_name)!!,
      channelAvatar = get<String>(streamData, Stream.avatar)!!,
      subscribersCount = get<String>(streamData, Stream.sub_count)!!
    )
  }
}

val MINI_PLAYER_HEIGHT = 62.dp

// -- Previews -----------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun StreamDescriptionPreview() {
  DescriptionSection(
    streamTitle = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
      "sed do eiusmod tempor incididunt ut labore et dolore magna ",
    views = "4.6M",
    date = "3y ago"
  )
}

@Preview(showBackground = true)
@Composable
fun StreamChannel1Preview() {
  ChannelSection(
    channelName = "Lorem",
    channelAvatar = "Channel Name",
    subscribersCount = "48M"
  )
}

@Preview(showBackground = true)
@Composable
fun StreamChannel2Preview() {
  ChannelSection(
    channelName = "Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet,",
    channelAvatar = "Channel Name",
    subscribersCount = "48M"
  )
}

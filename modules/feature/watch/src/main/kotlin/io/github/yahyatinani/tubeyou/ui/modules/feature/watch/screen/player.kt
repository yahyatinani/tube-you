package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.screen

import android.content.res.Configuration
import android.graphics.Color.TRANSPARENT
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.ControllerVisibilityListener
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.component.Thumbnail
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Grey300
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.events.RegPlaybackEvents
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.StreamState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fx.TyPlayer
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.subs.Stream
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
@OptIn(UnstableApi::class)
@kotlin.OptIn(ExperimentalMaterial3Api::class)
fun VideoPlayer(
  modifier: Modifier = Modifier,
  streamState: UIState,
  useController: Boolean = true,
  orientation: Int = LocalConfiguration.current.orientation
) {
  var showQualityControl: Boolean by remember { mutableStateOf(false) }
  var showQualitiesSheet: Boolean by remember { mutableStateOf(false) }

  val controllerVisibilityListener = remember(useController) {
    ControllerVisibilityListener { visibility: Int ->
      if (useController) {
        showQualityControl = visibility == View.VISIBLE
      }
    }
  }

  val streamData = streamState.data as IPersistentMap<Any, Any>
  val showThumbnail = get<Boolean>(streamData, "show_player_thumbnail") ?: false
  val thumbnail = get<String>(streamData, Stream.thumbnail)

  Surface(modifier = modifier, color = Color.Black) {
    Box {
      val playerState = streamData[common.state]
      LaunchedEffect(streamData) {
        if (playerState != null && playerState != StreamState.LOADING) {
          TyPlayer.playNewVideo(streamData)
        }
      }

      LaunchedEffect(orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
          dispatch(v(":player_fullscreen_landscape"))
        } else {
          dispatch(v(":player_portrait"))
        }
      }

      RegPlaybackEvents()
      AndroidView(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.Center),
        factory = { factoryContext ->
          PlayerView(factoryContext).apply {
            player = TyPlayer.getInstance()
            setShutterBackgroundColor(TRANSPARENT)
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
              resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
              setBackgroundColor(Color.Black.toArgb())
            }
          }
        },
        onRelease = {
          // Release the player instance since it outlives this view and prevent
          // MainActivity from being GCed (Leak on rotation).
          it.player = null
        },
        update = {
          it.setControllerVisibilityListener(controllerVisibilityListener)

          if (it.videoSurfaceView != null) {
            it.videoSurfaceView!!.layoutParams.height = MATCH_PARENT
          }

          it.useController = useController && showThumbnail != true
        }
      )

      if (showThumbnail) {
        Thumbnail(
          modifier = Modifier.wrapContentSize(),
          url = thumbnail
        )
      }

      if (
        playerState == StreamState.LOADING ||
        playerState == StreamState.BUFFERING
      ) {
        CircularProgressIndicator(
          modifier = Modifier
            .align(Alignment.Center)
            .size(64.dp),
          color = Grey300.copy(alpha = .4f)
        )
      }

      if (showQualityControl &&
        playerState != StreamState.LOADING &&
        playerState != null
      ) {
        TextButton(
          modifier = Modifier.align(Alignment.TopEnd),
          colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
          onClick = {
            dispatch(v("stream_panel_fsm", "generate_quality_list"))
            showQualitiesSheet = true
          }
        ) {
          Text(text = get<String>(streamData, Stream.current_quality)!!)
        }

        IconButton(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 40.dp, bottom = 6.dp),
          onClick = { dispatch(v(":toggle_orientation")) }
        ) {
          Image(
            imageVector = TyIcons.Fullscreen,
            contentDescription = "",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(
              MaterialTheme.colorScheme.onBackground
            )
          )
        }
      }
    }
  }

  if (showQualitiesSheet) {
    val sheetState =
      rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
      modifier = Modifier
//          .offset(y = (-24).dp)
        .padding(horizontal = 10.dp)
        .fillMaxWidth()
        .wrapContentHeight(),
      dragHandle = null,
      shape = RoundedCornerShape(10.dp),
      containerColor = TyTheme.colors.popupContainer,
      onDismissRequest = { showQualitiesSheet = false },
      sheetState = sheetState
    ) {
      val resolutions =
        get<List<Pair<String, Int>>>(streamData, Stream.quality_list)!!
      QualityList(
        resolutions = resolutions,
        containerColor = TyTheme.colors.popupContainer
      )
    }
  }
}

@Composable
fun MiniPlayerControls(
  modifier: Modifier = Modifier,
  isPlaying: Boolean,
  onClickClose: () -> Unit = { },
  playPausePlayer: () -> Unit = { }
) {
  /*  with(LocalContext.current.findWindow()) {
      this?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }*/
  Row(
    modifier = modifier.height(110.dp - 48.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    val colorFilter = ColorFilter.tint(
      color = MaterialTheme.colorScheme.onBackground
    )
    IconButton(onClick = playPausePlayer) {
      Image(
        imageVector = when (isPlaying) {
          true -> TyIcons.Pause
          else -> TyIcons.PlayArrow
        },
        modifier = Modifier.size(34.dp),
        contentDescription = "play/pause",
        colorFilter = colorFilter
      )
    }

    IconButton(
      modifier = Modifier.testTag("watch:close_mini_player_btn"),
      onClick = onClickClose
    ) {
      Image(
        imageVector = TyIcons.Close,
        modifier = Modifier
          .size(34.dp),
        contentDescription = "close video",
        colorFilter = colorFilter
      )
    }
  }
}

val MINI_PLAYER_HEIGHT = 56.dp
val MINI_PLAYER_WIDTH = 136.dp

package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.github.yahyatinani.tubeyou.modules.panel.common.R
import io.github.yahyatinani.y.core.collections.IPersistentMap

@SuppressLint("NewApi")
@Composable
@OptIn(UnstableApi::class)
fun VideoView(
  modifier: Modifier = Modifier,
  streamData: IPersistentMap<Any, Any>?,
  isCollapsed: Boolean,
  context: Context = LocalContext.current
) {
  val orientation = LocalConfiguration.current.orientation
  AndroidView(
    modifier = modifier.apply {
      if (orientation == ORIENTATION_LANDSCAPE) {
        padding(start = 26.dp)
      }
    },
    factory = {
      regPlaybackFxs()
      regPlaybackEvents()

      PlayerView(context).apply {
        setBackgroundColor(ContextCompat.getColor(context, R.color.black))
      }
    }
  ) {
    it.player = getExoPlayer(context, streamData)
    it.useController = !isCollapsed || orientation == ORIENTATION_LANDSCAPE
  }
}

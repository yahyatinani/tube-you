package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem.fromUri
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.panel.common.Stream
import io.github.yahyatinani.recompose.clearFx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.recompose.dispatchSync
import io.github.yahyatinani.recompose.regFx
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

var exoPlayer: ExoPlayer? = null

val listener = object : Player.Listener {
  override fun onIsPlayingChanged(isPlaying: Boolean) {
    dispatch(v("is_playing", isPlaying))
  }

  override fun onTracksChanged(tracks: Tracks) {
    println("onTracksChanged")
    // Update UI using current tracks.
  }

  override fun onPlaybackStateChanged(playbackState: Int) {
    if (playbackState == Player.STATE_READY) {
      dispatchSync(v("hidePlayerThumbnail"))
    }
  }
}

private fun closePlayer() {
  exoPlayer?.removeListener(listener)
  if (exoPlayer?.isCommandAvailable(Player.COMMAND_RELEASE) == true) {
    exoPlayer?.release()
    exoPlayer = null
    dispatchSync(v("showPlayerThumbnail"))
  }
}

@OptIn(UnstableApi::class)
fun getExoPlayer(
  context: Context,
  streamData: IPersistentMap<Any, Any>?
): ExoPlayer? {
  if (streamData == null) {
    closePlayer()
    return null
  }

  if (exoPlayer != null) return exoPlayer

  val dataSourceFactory: DataSource.Factory =
    DefaultHttpDataSource.Factory()
  val videoSource: MediaSource =
    ProgressiveMediaSource.Factory(dataSourceFactory)
      .createMediaSource(fromUri(get<String>(streamData, Stream.video_uri)!!))
  val audioSource: MediaSource =
    ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
      fromUri(get<String>(streamData, Stream.audio_uri)!!)
    )

  val mergeSource: MediaSource = MergingMediaSource(videoSource, audioSource)

  val trackSelector = DefaultTrackSelector(
    context,
    AdaptiveTrackSelection.Factory()
  )

  exoPlayer = ExoPlayer
    .Builder(context)
    .setTrackSelector(trackSelector)
    .setSeekForwardIncrementMs(5000L)
    .setSeekBackIncrementMs(5000L)
    .build()
    .apply {
      setMediaSource(mergeSource)
      playWhenReady = true
      addListener(listener)
      prepare()
    }

  return exoPlayer
}

@OptIn(UnstableApi::class)
class TyPlayer(exoPlayer: ExoPlayer, context: Context) : Player.Listener {
  val trackSelector: DefaultTrackSelector = DefaultTrackSelector(
    context,
    AdaptiveTrackSelection.Factory()
  )
}

fun regPlaybackFxs() {
  regFx(common.close_player) {
    GlobalScope.launch(Dispatchers.Main) {
      if (exoPlayer == null) return@launch
      closePlayer()
      clearFx(common.close_player)
    }
  }

  regFx(common.toggle_player) {
    val isPlaying = exoPlayer?.isPlaying == true
    dispatch(v("is_playing", isPlaying))
    if (isPlaying) exoPlayer?.pause() else exoPlayer?.play()
  }
}

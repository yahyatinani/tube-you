package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.annotation.UiThread
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.github.yahyatinani.tubeyou.modules.panel.common.Stream
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.v

@OptIn(UnstableApi::class)
object TyPlayer {
  private var exoPlayer: ExoPlayer? = null
  private var trackSelector: DefaultTrackSelector? = null

  private val listener = object : Player.Listener {
    override fun onIsPlayingChanged(isPlaying: Boolean) {
      if (exoPlayer!!.playbackState != Player.STATE_BUFFERING) {
        dispatch(v("playback_fsm", if (isPlaying) "on_play" else "on_pause"))
      }
    }

    override fun onTracksChanged(tracks: Tracks) {
      // Update UI using current tracks.
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
      if (playbackState == Player.STATE_READY) {
        val currentQuality = exoPlayer!!.videoSize.height
        dispatch(v("playback_fsm", Player.STATE_READY, currentQuality))
      } else if (playbackState == Player.STATE_BUFFERING) {
        dispatch(v("playback_fsm", Player.STATE_BUFFERING))
      }
    }
  }

  fun initInstance(context: Context) {
    if (exoPlayer == null) {
      trackSelector =
        DefaultTrackSelector(context, AdaptiveTrackSelection.Factory())

      val parameters = DefaultTrackSelector.ParametersBuilder()
        .setMaxVideoSize(Int.MAX_VALUE, Int.MAX_VALUE)
        .build()
      trackSelector!!.parameters = parameters

      exoPlayer = ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector!!)
        .setMediaSourceFactory(
          DefaultMediaSourceFactory(DefaultHttpDataSource.Factory())
        )
        .setSeekForwardIncrementMs(5000L)
        .setSeekBackIncrementMs(5000L)
        .setHandleAudioBecomingNoisy(true)
        .build()
    }
    exoPlayer!!.apply {
      playWhenReady = true
    }
  }

  fun getInstance(): Player = exoPlayer!!

  private fun mediaMetadata(streamData: IPersistentMap<Any, Any>?) =
    MediaMetadata.Builder()
      .setTitle(get<String>(streamData, Stream.title)!!)
      .setArtist(get<String>(streamData, Stream.uploader)!!)
      .setArtworkUri(get<String>(streamData, Stream.thumbnail)!!.toUri())
      //            .setExtras(extras)
      .build()

  @UiThread
  fun playNewVideo(streamData: IPersistentMap<Any, Any>?) {
    if (streamData == null) return
    val mediaItem = MediaItem.Builder()
      .setUri(get<String>(streamData, Stream.video_uri)!!.toUri())
      .setMimeType(MimeTypes.APPLICATION_M3U8)
//      .setMimeType(MimeTypes.VIDEO_VP9)
//    .setSubtitleConfigurations(subtitles)
      .setMediaMetadata(mediaMetadata(streamData))
      .build()

    if (mediaItem == exoPlayer!!.currentMediaItem) return

    exoPlayer!!.apply {
      addListener(listener)
      playWhenReady = true
      setMediaItem(mediaItem)
      prepare()
    }
  }

  @UiThread
  fun play() = exoPlayer!!.play()

  @UiThread
  fun pause() = exoPlayer!!.pause()

  @UiThread
  fun togglePlayPause() {
    if (exoPlayer!!.isPlaying) pause() else play()
  }

  @UiThread
  fun availableResolutions(): List<Pair<String, Int>> = exoPlayer!!
    .currentTracks.groups
    .asSequence()
    .flatMap { group ->
      (0 until group.length).map {
        group.getTrackFormat(it).height
      }
    }
    .filter { it > 0 }
    .map { "${it}p" to it }
    .toSortedSet(compareByDescending { it.second }).toList()

  @UiThread
  fun currentResolution(): Int = exoPlayer!!.videoSize.height

  @UiThread
  fun setResolution(resolution: Int) {
    trackSelector!!.setParameters(
      trackSelector!!.buildUponParameters().apply {
        setMaxVideoSize(Int.MAX_VALUE, resolution)
        setMinVideoSize(Int.MIN_VALUE, resolution)
      }
    )
  }

  fun close() {
    exoPlayer!!.removeListener(listener)
    exoPlayer!!.removeMediaItems(0, exoPlayer!!.mediaItemCount)
  }
}

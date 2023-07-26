package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm

import androidx.media3.common.Player
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.StreamData
import com.github.yahyatinani.tubeyou.modules.panel.common.ratio
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.selectKeys
import io.github.yahyatinani.y.core.v

enum class StreamState { LOADING, BUFFERING, PLAYING, PAUSED }

fun fetchStream(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val activeStream = event[1] as VideoViewModel

  return m(
    fsm.state_map to state!!
      .assoc("active_stream", activeStream)
      .assoc("show_player_thumbnail", true)
      .assoc("stream_data", StreamData(title = activeStream.title)),
    fx to v(
      v(BuiltInFx.dispatch, v("load_stream", activeStream.id)),
      v("remove_track")
    )
  )
}

fun pausePlayer(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(fx to v(v(common.close_player)))

fun playNewStream(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val streamData = event[1] as StreamData

  val videos = streamData.videoStreams.filter { it.videoOnly }

  val r = if (streamData.livestream) 16 / 9f else ratio(streamData)

  val (w, h) = get<Pair<Float, Float>>(appDb, ":screen_dimen_px")!!

  return m(
    fsm.state_map to state!!
      .assoc(
        "stream_data",
        streamData.copy(videoStreams = videos)
      )
      .assoc(":desc_sheet_height", h - (w / r))
  )
}

fun isSameVideoAlreadyPlaying(
  appDb: AppDb,
  state: State?,
  event: Event
): Boolean {
  val videoViewModel = event[1] as VideoViewModel
  return state?.get("active_stream") == videoViewModel
}

fun toggle(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v(common.toggle_player)))

fun closePlayer(appDb: AppDb, state: State?, event: Event): Effects = m(
  fsm.state_map to selectKeys(state, l(fsm._state)),
  fx to v(v(common.close_player))
)

fun hideThumbnail(appDb: AppDb, state: State?, event: Event): Effects = m(
  fsm.state_map to (state?.dissoc("show_player_thumbnail") ?: state)
)

fun setCurrentQuality(appDb: AppDb, state: State?, event: Event): Effects = m(
  fsm.state_map to (state?.assoc("current_quality", event[1]) ?: state)
)

fun generateQualityList(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v("quality_list")))

fun setQualityList(appDb: AppDb, state: State?, event: Event): Effects = m(
  fsm.state_map to state!!.assoc("quality_list", event[1])
    .assoc("current_quality", event[2])
)

fun setStreamComments(appDb: AppDb, state: State?, event: Event): Effects {
  if (state!!.containsKey("stream_comments")) return m()

  val (_, comments) = event
  return m(fsm.state_map to state.assoc("stream_comments", comments))
}

val playerMachine = m<Any?, Any?>(
  null to m(
    common.play_video to m(
      fsm.target to StreamState.LOADING,
      fsm.actions to v(::fetchStream)
    )
  ),
  StreamState.LOADING to m(
    "launch_stream" to m(
      fsm.target to StreamState.BUFFERING,
      fsm.actions to ::playNewStream
    )
  ),
  StreamState.PLAYING to m(
    "toggle_play_pause" to m(
      fsm.target to StreamState.PAUSED,
      fsm.actions to ::toggle
    ),
    Player.STATE_BUFFERING to m(fsm.target to StreamState.BUFFERING),
    "on_pause" to m(fsm.target to StreamState.PAUSED)
  ),
  StreamState.PAUSED to m(
    "toggle_play_pause" to m(
      fsm.target to StreamState.PLAYING,
      fsm.actions to ::toggle
    ),
    "on_play" to m(fsm.target to StreamState.PLAYING)
  ),
  fsm.ALL to m(
    common.play_video to v(
      m(fsm.target to fsm.ALL, fsm.guard to ::isSameVideoAlreadyPlaying),
      m(
        fsm.target to StreamState.LOADING,
        fsm.actions to v(::fetchStream, ::pausePlayer)
      )
    ),
    common.close_player to m(
      fsm.target to null,
      fsm.actions to ::closePlayer
    ),
    Player.STATE_READY to m(
      fsm.target to StreamState.PLAYING,
      fsm.actions to v(::hideThumbnail, ::setCurrentQuality)
    ),
    "generate_quality_list" to m(
      fsm.target to fsm.ALL,
      fsm.actions to ::generateQualityList
    ),
    "set_quality_list" to m(
      fsm.target to fsm.ALL,
      fsm.actions to ::setQualityList
    ),
    "set_stream_comments" to m(
      fsm.target to fsm.ALL,
      fsm.actions to ::setStreamComments
    )
  )
)

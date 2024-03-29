package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.media3.common.Player
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.AppDb
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fsm.fsm.actions
import io.github.yahyatinani.recompose.fsm.fsm.target
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamData
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

enum class StreamState { LOADING, BUFFERING, PLAYING, PAUSED }

fun fetchStream(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val activeStream = event[1] as VideoVm

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

fun playNewStream(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val streamData = event[1] as StreamData
  val videos = streamData.videoStreams.filter { it.videoOnly }
  return m(
    fsm.state_map to state!!
      .assoc(
        "stream_data",
        streamData.copy(videoStreams = videos)
      )
  )
}

fun isSameVideoAlreadyPlaying(
  appDb: AppDb,
  state: State?,
  event: Event
): Boolean {
  val videoViewModel = event[1] as VideoVm
  return state?.get("active_stream") == videoViewModel
}

fun toggle(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v(common.toggle_player)))

fun closePlayer(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v(common.close_player)))

fun stopPlayer(appDb: AppDb, state: State?, event: Event): Effects = m(
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

@OptIn(ExperimentalMaterial3Api::class)
val playerMachine = m<Any?, Any?>(
  null to m(
    common.play_video to m(
      target to StreamState.LOADING,
      actions to v(::fetchStream)
    )
  ),
  StreamState.LOADING to m(
    "launch_stream" to m(
      target to StreamState.BUFFERING,
      actions to ::playNewStream
    )
  ),
  StreamState.PLAYING to m(
    "toggle_play_pause" to m(
      target to StreamState.PAUSED,
      actions to ::toggle
    ),
    Player.STATE_BUFFERING to m(target to StreamState.BUFFERING),
    "on_pause" to m(target to StreamState.PAUSED)
  ),
  StreamState.PAUSED to m(
    "toggle_play_pause" to m(
      target to StreamState.PLAYING,
      actions to ::toggle
    ),
    "on_play" to m(target to StreamState.PLAYING)
  ),
  StreamState.BUFFERING to m(
    Player.STATE_READY to m(
      target to StreamState.PLAYING,
      actions to v(::hideThumbnail, ::setCurrentQuality)
    )
  ),
  fsm.ALL to m(
    common.play_video to v(
      m(target to fsm.ALL, fsm.guard to ::isSameVideoAlreadyPlaying),
      m(
        target to StreamState.LOADING,
        actions to v(::fetchStream, ::stopPlayer)
      )
    ),
    common.close_player to m(target to null, actions to ::closePlayer),
    SheetValue.Hidden to m(target to null, actions to ::closePlayer),
    "generate_quality_list" to m(
      target to fsm.ALL,
      actions to ::generateQualityList
    ),
    "set_quality_list" to m(
      target to fsm.ALL,
      actions to ::setQualityList
    )
  )
)

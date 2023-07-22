package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.media3.common.Player
import androidx.paging.LoadState.Loading
import androidx.paging.LoadState.NotLoading
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.StreamComment
import com.github.yahyatinani.tubeyou.modules.panel.common.StreamComments
import com.github.yahyatinani.tubeyou.modules.panel.common.StreamData
import com.github.yahyatinani.tubeyou.modules.panel.common.ratio
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fsm.fsm.actions
import io.github.yahyatinani.recompose.fsm.fsm.guard
import io.github.yahyatinani.recompose.fsm.fsm.target
import io.github.yahyatinani.recompose.fx.BuiltInFx.dispatch
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.y.core.collections.PersistentVector
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.selectKeys
import io.github.yahyatinani.y.core.v

enum class PlayerState { LOADING, BUFFERING, PLAYING, PAUSED }

fun fetchStream(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val videoViewModel = event[1] as VideoViewModel
  return m(
    fsm.state_map to state!!
      .assoc("videoVm", videoViewModel)
      .assoc("show_player_thumbnail", true)
      .dissoc("stream_data")
      .dissoc("stream_comments"),
    fx to v(
      v(dispatch, v("load_stream", videoViewModel.id)),
      v("remove_track")
    )
  )
}

fun pausePlayer(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(fx to v(v(common.pause_player)))

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
    fsm.state_map to state!!.assoc(
      "stream_data",
      streamData.copy(videoStreams = videos)
    ).assoc(":desc_sheet_height", h - (w / r))
  )
}

fun isSameVideoAlreadyPlaying(
  appDb: AppDb,
  state: State?,
  event: Event
): Boolean {
  val videoViewModel = event[1] as VideoViewModel
  return state?.get("videoVm") == videoViewModel
}

fun toggle(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v(common.toggle_player)))

fun releasePlayer(appDb: AppDb, state: State?, event: Event): Effects = m(
  fsm.state_map to selectKeys(state, l(fsm._state)),
  fx to v(v(common.release_player))
)

fun hidePlayerSheet(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v(common.hide_player_sheet)))

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
  println("djsflkjsdfj ${event.count}")
  return m(fsm.state_map to state.assoc("stream_comments", comments))
}

val playerMachine = m<Any?, Any?>(
  null to m(
    common.play_video to m(
      target to PlayerState.LOADING,
      actions to ::fetchStream
    )
  ),
  PlayerState.LOADING to m(
    "launch_stream" to m(
      target to PlayerState.BUFFERING,
      actions to ::playNewStream
    )
  ),
  PlayerState.PLAYING to m(
    "toggle_play_pause" to m(target to PlayerState.PAUSED, actions to ::toggle),
    Player.STATE_BUFFERING to m(target to PlayerState.BUFFERING),
    "on_pause" to m(target to PlayerState.PAUSED)
  ),
  PlayerState.PAUSED to m(
    "toggle_play_pause" to m(
      target to PlayerState.PLAYING,
      actions to ::toggle
    ),
    "on_play" to m(target to PlayerState.PLAYING)
  ),
  fsm.ALL to m(
    common.play_video to v(
      m(target to fsm.ALL, guard to ::isSameVideoAlreadyPlaying),
      m(
        target to PlayerState.LOADING,
        actions to v(::fetchStream, ::pausePlayer)
      )
    ),
    common.release_player to m(target to null, actions to ::releasePlayer),
    Player.STATE_READY to m(
      target to PlayerState.PLAYING,
      actions to v(::hideThumbnail, ::setCurrentQuality)
    ),
    "generate_quality_list" to m(
      target to fsm.ALL,
      actions to ::generateQualityList
    ),
    "set_quality_list" to m(
      target to fsm.ALL,
      actions to ::setQualityList
    ),
    "set_stream_comments" to m(
      target to fsm.ALL,
      actions to ::setStreamComments
    )
  )
)

// -------------

enum class PlayerSheetState { HIDDEN, COLLAPSED, EXPANDED }

fun expandPlayerSheet(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(fx to v(v(common.expand_player_sheet)))

@OptIn(ExperimentalMaterial3Api::class)
val bottomSheetMachine = m(
  PlayerSheetState.EXPANDED to m(
    SheetValue.PartiallyExpanded to m(target to PlayerSheetState.COLLAPSED)
  ),
  PlayerSheetState.COLLAPSED to m(
    common.expand_player_sheet to m(
      target to PlayerSheetState.EXPANDED,
      actions to ::expandPlayerSheet
    )
  ),
  fsm.ALL to m(
    common.play_video to m(
      target to PlayerSheetState.EXPANDED,
      actions to ::expandPlayerSheet
    ),
    SheetValue.Expanded to m(
      target to PlayerSheetState.EXPANDED,
      actions to ::expandPlayerSheet
    ),
    "close_player" to m(
      target to PlayerSheetState.HIDDEN,
      actions to ::hidePlayerSheet
    ),
    SheetValue.Hidden to m(
      target to PlayerSheetState.HIDDEN,
      actions to ::hidePlayerSheet
    )
  )
)

// -------------

fun appendStreamComments(appDb: AppDb, state: State?, event: Event): Effects {
  if (!state!!.containsKey("stream_comments")) return m()

  val (_, comments) = event

  val streamComments = get<StreamComments>(state, "stream_comments")!!

  return m(
    fsm.state_map to state.assoc(
      "stream_comments", streamComments.copy(
        comments = comments as PersistentVector<StreamComment>
      )
    )
  )
}

enum class CommentsSheetState {
  LOADING, REFRESHING, LOADED, APPENDING
}

val commentsMachine = m(
  null to m(
    common.play_video to m(
      target to CommentsSheetState.LOADING
    )
  ),
  CommentsSheetState.LOADING to m(
    "set_stream_comments" to m(
      target to CommentsSheetState.LOADED
    )
  ),
  CommentsSheetState.LOADED to m(
    common.play_video to m(
      target to CommentsSheetState.LOADING
    ),
    Loading to m(target to CommentsSheetState.APPENDING)
  ),
  CommentsSheetState.APPENDING to m(
    "append_comments_page" to m(
      target to CommentsSheetState.LOADED,
      actions to ::appendStreamComments
    ),
    NotLoading(endOfPaginationReached = true) to m(
      target to CommentsSheetState.LOADED
    )
  ),
  fsm.ALL to m(
    "close_player" to m(target to null)
  )
)

// -------------

val playbackMachine = m(
  fsm.type to fsm.parallel,
  fsm.regions to v(
    v(":player", playerMachine),
    v(":player_sheet", bottomSheetMachine),
    v(":comments_sheet", commentsMachine)
  )
)

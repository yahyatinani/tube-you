package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm

import androidx.paging.LoadState
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.StreamComment
import com.github.yahyatinani.tubeyou.modules.panel.common.StreamComments
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.y.core.collections.PersistentVector
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun appendStreamComments(appDb: AppDb, state: State?, event: Event): Effects {
  if (!state!!.containsKey("stream_comments")) return m()

  val (_, comments) = event

  val streamComments = get<StreamComments>(state, "stream_comments")!!

  return m(
    fsm.state_map to state.assoc(
      "stream_comments",
      streamComments.copy(
        comments = comments as PersistentVector<StreamComment>
      )
    )
  )
}

fun fetchStreamComments(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val videoViewModel = state!!["active_stream"] as VideoViewModel
  return m(
    fsm.state_map to state.dissoc("stream_comments"),
    BuiltInFx.fx to v(
      v(
        BuiltInFx.dispatch,
        v("load_stream_comments", videoViewModel.id)
      )
    )
  )
}

enum class CommentsListState { LOADING, REFRESHING, LOADED, APPENDING }

val commentsListMachine = m(
  CommentsListState.LOADING to m(
    "set_stream_comments" to m(fsm.target to CommentsListState.LOADED)
  ),
  CommentsListState.LOADED to m(
    LoadState.Loading to m(fsm.target to CommentsListState.APPENDING),
    "refresh_comments" to m(
      fsm.target to CommentsListState.REFRESHING,
      fsm.actions to ::fetchStreamComments
    )
  ),
  CommentsListState.REFRESHING to m(
    "set_stream_comments" to m(
      fsm.target to CommentsListState.LOADED
    )
  ),
  CommentsListState.APPENDING to m(
    "append_comments_page" to m(
      fsm.target to CommentsListState.LOADED,
      fsm.actions to ::appendStreamComments
    ),
    LoadState.NotLoading(endOfPaginationReached = true) to m(
      fsm.target to CommentsListState.LOADED
    )
  ),
  fsm.ALL to m(
    common.play_video to m(
      fsm.target to CommentsListState.LOADING,
      fsm.actions to ::fetchStreamComments
    ),
    "close_player" to m(fsm.target to null),
    common.close_player to m(fsm.target to null)
  )
)

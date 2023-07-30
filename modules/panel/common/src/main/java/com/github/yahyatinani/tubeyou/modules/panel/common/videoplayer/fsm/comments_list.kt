package com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm

import androidx.paging.LoadState
import androidx.paging.LoadState.NotLoading
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.StreamComment
import com.github.yahyatinani.tubeyou.modules.panel.common.StreamComments
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fsm.fsm.actions
import io.github.yahyatinani.recompose.fsm.fsm.target
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
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
    fx to v(
      v(
        BuiltInFx.dispatch,
        v("load_stream_comments", videoViewModel.id)
      )
    )
  )
}

enum class CommentsListState { LOADING, REFRESHING, READY, APPENDING }

val commentsListMachine = m(
  CommentsListState.LOADING to m(
    "set_stream_comments" to m(target to CommentsListState.READY)
  ),
  CommentsListState.READY to m(
    v("append_comments", LoadState.Loading) to m(
      target to CommentsListState.APPENDING
    ),
    "refresh_comments" to m(
      target to CommentsListState.REFRESHING,
      actions to ::fetchStreamComments
    )
  ),
  CommentsListState.REFRESHING to m(
    "set_stream_comments" to m(
      target to CommentsListState.READY
    )
  ),
  CommentsListState.APPENDING to m(
    "append_comments_page" to m(
      target to CommentsListState.READY,
      actions to ::appendStreamComments
    ),
    v("append_comments", NotLoading(endOfPaginationReached = true)) to m(
      target to CommentsListState.READY
    )
  ),
  fsm.ALL to m(
    common.play_video to m(
      target to CommentsListState.LOADING,
      actions to ::fetchStreamComments
    ),
    "close_player" to m(target to null),
    common.close_player to m(target to null)
  )
)

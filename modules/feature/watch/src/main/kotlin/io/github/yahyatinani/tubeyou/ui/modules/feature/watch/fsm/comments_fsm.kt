package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm

import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fsm.fsm.actions
import io.github.yahyatinani.recompose.fsm.fsm.target
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.tubeyou.common.AppDb
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamComment
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamComments
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun setStreamComments(appDb: AppDb, state: State?, event: Event): Effects {
  val (_, comments) = event
  return m(fsm.state_map to state!!.assoc("stream_comments", comments))
}

fun appendStreamComments(appDb: AppDb, state: State?, event: Event): Effects {
  if (!state!!.containsKey("stream_comments")) return m()

  val (_, comments) = event

  val streamComments = get<StreamComments>(state, "stream_comments")!!

  return m(
    fsm.state_map to state.assoc(
      "stream_comments",
      streamComments.copy(
        comments = comments as List<StreamComment>
      )
    )
  )
}

fun fetchStreamComments(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val videoViewModel = state!!["active_stream"] as VideoVm
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

fun appendCommentsError(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val (_, error) = event
  println("dsjfkjsdjfjsd $error")
  return m()
}

fun loadingCommentsError(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val (_, error) = event
  println("sdsgsdagf $error")
  return m()
}

enum class ListState { LOADING, REFRESHING, READY, APPENDING }

val commentsListMachine = m(
  ListState.LOADING to m(
    "set_stream_comments" to m(
      target to ListState.READY,
      actions to ::setStreamComments
    ),
    v("append_comments", "error") to m(
      target to ListState.LOADING,
      actions to ::loadingCommentsError
    )
  ),
  ListState.READY to m(
    v("append_comments", "loading") to m(
      target to ListState.APPENDING
    ),
    "refresh_comments" to m(
      target to ListState.REFRESHING,
      actions to ::fetchStreamComments
    )
  ),
  ListState.REFRESHING to m(
    "set_stream_comments" to m(
      target to ListState.READY,
      actions to ::setStreamComments
    ),
    v("append_comments", "error") to m(
      target to ListState.REFRESHING,
      actions to ::loadingCommentsError
    )
  ),
  ListState.APPENDING to m<Any, Any>(
    v("append_comments", "done_loading") to m(
      target to ListState.READY,
      actions to ::appendStreamComments
    ),
    v("append_comments", "error") to m(
      target to ListState.READY,
      actions to ::appendCommentsError
    )
  ),
  fsm.ALL to m(
    common.play_video to m(
      target to ListState.LOADING,
      actions to ::fetchStreamComments
    ),
    common.close_player to m(target to null)
  )
)

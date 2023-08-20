package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm

import androidx.paging.LoadState
import androidx.paging.LoadState.NotLoading
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.AppDb
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fsm.fsm.actions
import io.github.yahyatinani.recompose.fsm.fsm.guard
import io.github.yahyatinani.recompose.fsm.fsm.target
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamComment
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamComments
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

const val COMMENTS_ROUTE = "comments_route"
const val REPLIES_ROUTE = "replies_route"

fun selectComment(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val (_, comment) = event
  return m(fsm.state_map to state!!.assoc("selected_comment", comment))
}

fun fetchCommentReplies(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val (index) = state!!["selected_comment"] as Pair<Int, UIState>
  val repliesPage =
    get<StreamComments>(state, "stream_comments")!!.comments[index].repliesPage
  val videoViewModel = state["active_stream"] as VideoVm
  val streamId = videoViewModel.id.replace("/watch?v=", "")

  return m(
    fx to v(
      v(
        BuiltInFx.dispatch_later,
        m(
          BuiltInFx.ms to 300,
          BuiltInFx.dispatch to v("load_comment_replies", streamId, repliesPage)
        )
      )
    )
  )
}

fun navToReplies(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(
  fsm.state_map to state!!.assoc("comments_panel_route", REPLIES_ROUTE),
  fx to v(v("nav_comment_replies"))
)

fun navBackToComments(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(
  fsm.state_map to state!!
    .assoc("comments_panel_route", COMMENTS_ROUTE)
    .dissoc("selected_comment")
    .dissoc("comment_replies")
)

fun appendCommentReplies(appDb: AppDb, state: State?, event: Event): Effects {
  if (state?.get("comments_panel_route") != REPLIES_ROUTE) return m()

  val (_, replies) = event

  return m(
    fsm.state_map to state.assoc(
      "comment_replies",
      replies as List<StreamComment>
    )
  )
}

fun anyReplies(
  appDb: AppDb,
  state: State?,
  event: Event
): Boolean {
  val (_, comment) = event
  val (index) = comment as Pair<Int, UIState>
  val repliesPage =
    get<StreamComments>(state, "stream_comments")!!.comments[index].repliesPage
  return repliesPage != null
}

fun clearReplies(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects = m(
  fsm.state_map to state!!
    .assoc("comments_panel_route", COMMENTS_ROUTE)
    .dissoc("selected_comment")
    .dissoc("comment_replies")
)

val commentRepliesListMachine = m(
  null to m(
    "navigate_replies" to v(
      m(
        target to ListState.LOADING,
        guard to ::anyReplies,
        actions to v(::selectComment, ::navToReplies, ::fetchCommentReplies)
      ),
      m(
        target to ListState.READY,
        actions to v(::selectComment, ::navToReplies)
      )
    )
  ),
  ListState.READY to m(
    v("append_replies", LoadState.Loading) to m(
      target to ListState.APPENDING
    ),
    "refresh_comment_replies" to m(
      target to ListState.REFRESHING,
      actions to v(::fetchCommentReplies)
    )
  ),
  ListState.REFRESHING to m(
    "append_replies_page" to m(
      target to ListState.READY,
      actions to ::appendCommentReplies
    )
  ),
  ListState.LOADING to m(
    "append_replies_page" to m(
      target to ListState.READY,
      actions to ::appendCommentReplies
    )
  ),
  ListState.APPENDING to m(
    "append_replies_page" to m(
      target to ListState.READY,
      actions to ::appendCommentReplies
    ),
    v("append_replies", NotLoading(endOfPaginationReached = true)) to m(
      target to ListState.READY
    )
  ),
  fsm.ALL to m(
    common.play_video to m(target to null, actions to ::clearReplies),
    "nav_back_to_comments" to m(
      target to null,
      actions to ::navBackToComments
    ),
    "close_comments_sheet" to m(target to null, actions to ::clearReplies),
    common.close_player to m(target to null)
  )
)

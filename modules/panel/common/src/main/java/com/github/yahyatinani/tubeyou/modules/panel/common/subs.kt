package com.github.yahyatinani.tubeyou.modules.panel.common

import android.content.Context
import android.text.SpannedString
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.media3.common.MimeTypes
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.designsystem.component.MEDIUM_BULLET
import com.github.yahyatinani.tubeyou.modules.designsystem.core.formatSubCount
import com.github.yahyatinani.tubeyou.modules.designsystem.core.formatViews
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.panel.common.html.toAnnotatedString
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.createDashSource
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm.COMMENTS_ROUTE
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm.CommentsListState
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.fsm.StreamState
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.recompose.subs.Query
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.getIn
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime

enum class Stream {
  title,
  uploader,
  video_uri,
  aspect_ratio,
  thumbnail,
  quality_list,
  current_quality,
  mime_type,
  views,
  date,
  avatar,
  sub_count,
  channel_name,
  likes_count,
  description,
  height,
  comments,
  comments_count,
  highlight_comment,
  highlight_comment_avatar,
  comments_disabled
}

fun ratio(streamData: StreamData): Float {
  val default = 16 / 9f
  if (streamData.livestream) return default

  val stream = streamData.videoStreams.firstOrNull {
    val codec: String? = it.codec
    codec != null && codec.contains("avc1")
  } ?: return default

  val w = stream.width / 240f
  val h = stream.height / 240f

  val ar = w / h
  return if (ar < default) ar else default
}

/**
 * @param date eg. 2020-03-27
 */
fun timeAgoFormat(date: String): String {
  val localDate = date.toLocalDate().periodUntil(
    Clock.System.now().toLocalDateTime(
      TimeZone.currentSystemDefault()
    ).date
  )

  val years = localDate.years
  val months = localDate.months
  val days = localDate.days

  return when {
    years > 0 -> "${years}y ago"
    months > 0 -> "${months}m ago"
    days > 0 -> "${days}d ago"
    else -> "today"
  }
}

private fun shortenTime(uploaded: String): String = uploaded
  .replace(" years", "y")
  .replace(" months", "m")
  .replace(" days", "d")
  .replace(" hours", "h")

private fun highlightComment(sc: StreamComments) = when {
  sc.disabled -> m(
    Stream.comments_disabled to true,
    Stream.comments_count to ""
  )

  sc.comments.isEmpty() -> m(
    Stream.comments_count to "",
    Stream.highlight_comment to SpannedString("")
  )

  else -> {
    val firstComment = sc.comments[0]
    m(
      Stream.comments_count to formatSubCount(sc.commentCount),
      Stream.highlight_comment to HtmlCompat.fromHtml(
        firstComment.commentText,
        HtmlCompat.FROM_HTML_MODE_LEGACY
      ),
      Stream.highlight_comment_avatar to firstComment.thumbnail
    )
  }
}

private fun mapComment(
  comment: StreamComment,
  stream: StreamData?
) = m(
  "author" to comment.author,
  "commentedTime" to " $MEDIUM_BULLET ${shortenTime(comment.commentedTime)}",
  "author_avatar" to comment.thumbnail,
  "comment_text" to HtmlCompat.fromHtml(
    comment.commentText,
    HtmlCompat.FROM_HTML_MODE_LEGACY
  ).toAnnotatedString(),
  "likes_count" to formatSubCount(comment.likeCount),
  "replies_count" to comment.replyCount,
  "verified" to comment.verified,
  "pinned" to comment.pinned,
  "hearted" to comment.hearted,
  "uploader" to (stream?.uploader ?: "this channel"),
  "uploader_avatar" to (stream?.uploaderAvatar ?: ""),
  "by_uploader" to (comment.commentorUrl == stream?.uploaderUrl)
)

@OptIn(ExperimentalMaterial3Api::class)
fun regCommonSubs() {
  regSub(queryId = "active_stream_vm") { db: AppDb, _: Query ->
    get<VideoViewModel>(db["stream_panel_fsm"], "active_stream")
      ?: VideoViewModel()
  }

  regSub(queryId = "stream_panel_fsm") { db: AppDb, _: Query ->
    db["stream_panel_fsm"]
  }

  regSub<IPersistentMap<Any, Any>, UIState>(
    queryId = "active_stream",
    initialValue = UIState(m(common.state to StreamState.LOADING)),
    inputSignal = v("stream_panel_fsm")
  ) { streamPanelFsm: IPersistentMap<Any, Any>?, _, (_, context) ->
    val playbackMachine = get<Any>(streamPanelFsm, fsm._state)
    val playerRegion = get<Any>(playbackMachine, ":player")

    if (playerRegion == null || playerRegion == StreamState.LOADING) {
      return@regSub UIState(m(common.state to StreamState.LOADING))
    }

    val stream = get<StreamData>(streamPanelFsm, "stream_data")
    val currentQuality = streamPanelFsm!!["current_quality"]
    val ql = get<List<Pair<String, Int>>>(streamPanelFsm, "quality_list") ?: l()
    val qualityList = ql.map {
      val a = if (currentQuality == it.second) "${it.first} ✓" else it.first
      a to it.second
    }

    val cq = if (currentQuality == null) "" else "${currentQuality}p"

    val viewModel = get<VideoViewModel>(streamPanelFsm, "active_stream")!!

    val views = formatViews(stream!!.views)

    val views1 = if (viewModel.isLiveStream) {
      views + " watching" + " Started ${timeAgoFormat(stream.uploadDate)}"
    } else {
      "$views views"
    }

    val m = m<Any, Any?>(
      common.state to playerRegion,
      Stream.title to stream.title,
      Stream.uploader to stream.uploader,
      Stream.thumbnail to stream.thumbnailUrl,
      Stream.quality_list to qualityList,
      Stream.current_quality to cq,
      Stream.aspect_ratio to ratio(stream),
      Stream.views to views1,
      Stream.date to shortenTime(viewModel.uploaded),
      Stream.channel_name to viewModel.uploaderName,
      Stream.avatar to stream.uploaderAvatar,
      Stream.sub_count to formatSubCount(stream.uploaderSubscriberCount),
      Stream.likes_count to formatViews(stream.likes),
      Stream.description to stream.description
    )

    UIState(
      data = if (stream.videoStreams.isNotEmpty()) {
        when {
          stream.livestream && stream.dash != null -> {
            m.assoc(Stream.video_uri, stream.dash.toUri())
          }

          else -> {
            m.assoc(
              Stream.video_uri,
              createDashSource(stream, context as Context)
            )
          }
        }.assoc(Stream.mime_type, MimeTypes.APPLICATION_MPD)
      } else {
        m.assoc(Stream.video_uri, stream.hls!!.toUri())
          .assoc(Stream.mime_type, MimeTypes.APPLICATION_M3U8)
      }
    )
  }

  regSub("comments_sheet") { db: AppDb, _: Query ->
    val playbackFsm = db["stream_panel_fsm"]
    val playbackMachine = get<Any>(playbackFsm, fsm._state)
    val commentsSheet =
      get<SheetValue>(playbackMachine, ":comments_sheet")
    commentsSheet ?: SheetValue.Hidden
  }

  regSub("description_sheet") { db: AppDb, _: Query ->
    val playbackFsm = db["stream_panel_fsm"]
    val playbackMachine = get<Any>(playbackFsm, fsm._state)
    val commentsSheet =
      get<SheetValue>(playbackMachine, ":description_sheet")
    commentsSheet ?: SheetValue.Hidden
  }

  val loadingState = UIState(m(common.state to CommentsListState.LOADING))

  regSub<IPersistentMap<Any, Any>, UIState>(
    queryId = Stream.comments,
    initialValue = loadingState,
    inputSignal = v("stream_panel_fsm")
  ) { panelFsm: IPersistentMap<Any, Any>?, prev, _ ->
    val fsmStates = get<Any>(panelFsm, fsm._state)
    val commentsListState =
      get(fsmStates, ":comments_list") ?: CommentsListState.LOADING
    val commentsState: IPersistentMap<Any?, Any?> = when (commentsListState) {
      CommentsListState.LOADING -> m()
      CommentsListState.REFRESHING, CommentsListState.APPENDING -> {
        prev.data as IPersistentMap<Any?, Any?>
      }

      CommentsListState.READY -> {
        val sc = get<StreamComments>(panelFsm, "stream_comments")!!
        val stream = get<StreamData>(panelFsm, "stream_data")
        val comments = sc.comments.map { comment ->
          UIState(mapComment(comment, stream))
        }

        m(
          "comments_list" to comments,
          "comments_section" to highlightComment(sc)
        )
      }
    }

    UIState(data = commentsState.assoc(common.state, commentsListState))
  }

  regSub<IPersistentMap<Any, Any>, UIState>(
    queryId = common.comment_replies,
    initialValue = loadingState,
    inputSignal = v("stream_panel_fsm")
  ) { panelFsm: IPersistentMap<Any, Any>?, prev, _ ->
    val fsmStates = get<Any>(panelFsm, fsm._state)
    val commentRepliesState =
      get(fsmStates, ":comment_replies") ?: CommentsListState.LOADING

    val ret: IPersistentMap<Any?, Any?> = when (commentRepliesState) {
      CommentsListState.LOADING -> m()

      CommentsListState.READY -> {
        val replies = get<List<StreamComment>>(panelFsm, "comment_replies")
          ?: v()
        val stream = get<StreamData>(panelFsm, "stream_data")
        val comments = replies.map { comment ->
          UIState(mapComment(comment, stream))
        }

        val selectedComment =
          get<Pair<Int, UIState>>(panelFsm, "selected_comment")
        m(
          "replies_list" to comments,
          "selected_comment" to selectedComment!!.second
        )
      }

      CommentsListState.REFRESHING, CommentsListState.APPENDING -> {
        prev.data as IPersistentMap<Any?, Any?>
      }
    }
    UIState(ret.assoc(common.state, commentRepliesState))
  }

  regSub(queryId = "active_comments_route") { db: AppDb, _: Query ->
    getIn(db, l("stream_panel_fsm", "comments_panel_route")) ?: COMMENTS_ROUTE
  }

  regSub(
    queryId = "comments_panel",
    initialValue = UIState(
      m(
        "current_route" to COMMENTS_ROUTE,
        "comments" to loadingState,
        "replies" to loadingState
      )
    ),
    v(Stream.comments),
    v(common.comment_replies),
    v("active_comments_route")
  ) { (comments, replies, route), _, _ ->
    UIState(
      m("current_route" to route, "comments" to comments, "replies" to replies)
    )
  }
}

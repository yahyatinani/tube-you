package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.subs

import android.content.Context
import android.text.SpannedString
import android.text.TextPaint
import android.text.style.URLSpan
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.media3.common.MimeTypes
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.recompose.subs.Query
import io.github.yahyatinani.tubeyou.common.AppDb
import io.github.yahyatinani.tubeyou.core.viewmodels.MEDIUM_BULLET
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.core.viewmodels.formatChannel
import io.github.yahyatinani.tubeyou.core.viewmodels.formatPlayList
import io.github.yahyatinani.tubeyou.core.viewmodels.formatSubCount
import io.github.yahyatinani.tubeyou.core.viewmodels.formatVideo
import io.github.yahyatinani.tubeyou.core.viewmodels.formatViews
import io.github.yahyatinani.tubeyou.modules.core.network.Channel
import io.github.yahyatinani.tubeyou.modules.core.network.Playlist
import io.github.yahyatinani.tubeyou.modules.core.network.Video
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamComment
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamComments
import io.github.yahyatinani.tubeyou.modules.core.network.watch.StreamData
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.dash.createDashSource
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.COMMENTS_ROUTE
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.ListState
import io.github.yahyatinani.tubeyou.ui.modules.feature.watch.fsm.StreamState
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.collections.PersistentArrayMap
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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

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
  comments_disabled,
  views_full,
  year,
  month_day,
  related_streams
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

private fun shortenTime(uploaded: String): String =
  uploaded.replace(" years", "y")
    .let { if (it !== uploaded) return it else it }
    .replace(" year", "y").let { if (it !== uploaded) return it else it }
    .replace(" months", "m").let { if (it !== uploaded) return it else it }
    .replace(" month", "m").let { if (it !== uploaded) return it else it }
    .replace(" days", "d").let { if (it !== uploaded) return it else it }
    .replace(" day", "d").let { if (it !== uploaded) return it else it }
    .replace(" hours", "h").let { if (it !== uploaded) return it else it }
    .replace(" hour", "h").let { if (it !== uploaded) return it else it }
    .replace("minutes", "min")

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
): PersistentArrayMap<String, Any> {
  val byUploader = comment.commentorUrl == stream?.uploaderUrl
  val isVerified = when {
    byUploader -> stream!!.uploaderVerified
    else -> comment.verified
  }
  return m(
    "author" to (comment.author ?: ""),
    "commentedTime" to " $MEDIUM_BULLET ${shortenTime(comment.commentedTime)}",
    "author_avatar" to comment.thumbnail,
    "comment_text" to HtmlCompat.fromHtml(
      comment.commentText,
      HtmlCompat.FROM_HTML_MODE_LEGACY
    ).toAnnotatedString(),
    "likes_count" to formatSubCount(comment.likeCount),
    "replies_count" to comment.replyCount,
    "verified" to isVerified,
    "pinned" to comment.pinned,
    "hearted" to comment.hearted,
    "uploader" to (stream?.uploader ?: "this channel"),
    "uploader_avatar" to (stream?.uploaderAvatar ?: ""),
    "by_uploader" to byUploader
  )
}

fun formatFullViews(number: Long): String {
  val numberFormat = NumberFormat.getNumberInstance(Locale.US)
  return numberFormat.format(number)
}

fun formatToMonthDay(inputDate: String?): String? {
  if (inputDate == null) return "-"
  val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
  val outputFormat = SimpleDateFormat("MMM d", Locale.US)
  val date = inputFormat.parse(inputDate)
  return outputFormat.format(date!!)
}

private class URLSpanNoUnderline(url: String?) : URLSpan(url) {
  override fun updateDrawState(ds: TextPaint) {
    super.updateDrawState(ds)
    ds.isUnderlineText = false
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RegWatchSubs() {
  regSub(queryId = "stream_panel_fsm") { db: AppDb, _: Query ->
    db["stream_panel_fsm"]
  }

  regSub(queryId = "active_stream_vm") { db: AppDb, _: Query ->
    get<VideoVm>(db["stream_panel_fsm"], "active_stream")
      ?: VideoVm()
  }

  regSub<IPersistentMap<Any, Any>, UIState>(
    queryId = "active_stream",
    initialValue = UIState(m(common.state to StreamState.LOADING)),
    inputSignal = v("stream_panel_fsm")
  ) { streamPanelFsm: IPersistentMap<Any, Any>?, _, (_, context, screenDim) ->
    val machineState = get<Any>(streamPanelFsm, fsm._state)
    val playerRegion = get<Any>(machineState, ":player")
      ?: return@regSub UIState(m<Any, Any>())

    if (playerRegion == StreamState.LOADING) {
      return@regSub UIState(m(common.state to StreamState.LOADING))
    }

    val stream = get<StreamData>(streamPanelFsm, "stream_data")!!
    val ratio = ratio(stream)
    val (w, h) = (screenDim as Pair<Float, Float>)
    val currentQuality = streamPanelFsm!!["current_quality"]
    val ql = get<List<Pair<String, Int>>>(streamPanelFsm, "quality_list") ?: l()
    val qualityList = ql.map {
      val a = if (currentQuality == it.second) "${it.first} ✓" else it.first
      a to it.second
    }

    val cq = if (currentQuality == null) "" else "${currentQuality}p"

    val viewModel = get<VideoVm>(streamPanelFsm, "active_stream")!!

    val views = formatViews(stream.views).let {
      if (viewModel.isLiveStream) {
        it + " watching" + " Started ${timeAgoFormat(stream.uploadDate)}"
      } else {
        "$it views"
      }
    }

    val relatedStreams = stream.relatedStreams.map { item ->
      when (item) {
        is Video -> formatVideo(item, (context as Context).resources)
        is Channel -> formatChannel(item)
        is Playlist -> formatPlayList(item)
      }
    }
    val m = m(
      common.state to playerRegion,
      Stream.title to stream.title,
      Stream.uploader to stream.uploader,
      Stream.thumbnail to stream.thumbnailUrl,
      Stream.quality_list to qualityList,
      Stream.current_quality to cq,
      Stream.aspect_ratio to ratio,
      "height" to (
        stream.videoStreams.firstOrNull {
          val codec: String? = it.codec
          codec != null && codec.contains("avc1")
        }?.height ?: 0
        ),
      Stream.views to views,
      Stream.date to shortenTime(viewModel.uploaded),
      Stream.channel_name to viewModel.uploaderName,
      Stream.avatar to (stream.uploaderAvatar ?: ""),
      Stream.sub_count to formatSubCount(stream.uploaderSubscriberCount),
      Stream.likes_count to formatViews(stream.likes),
      Stream.views_full to formatFullViews(stream.views),
      Stream.year to stream.uploadDate.toLocalDate().year.toString(),
      Stream.month_day to formatToMonthDay(stream.uploadDate),
      Stream.description to HtmlCompat.fromHtml(
        stream.description,
        HtmlCompat.FROM_HTML_MODE_LEGACY
      ),
      Stream.related_streams to relatedStreams,
      ":desc_sheet_height" to h - (w / ratio)
    )

    UIState(
      data = if (stream.videoStreams.isNotEmpty()) {
        when {
          stream.livestream && stream.dash != null -> {
            m.assoc(Stream.video_uri, stream.dash!!.toUri())
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

  regSub("description_sheet") { db: AppDb, _: Query ->
    val playbackFsm = db["stream_panel_fsm"]
    val playbackMachine = get<Any>(playbackFsm, fsm._state)
    val commentsSheet =
      get<SheetValue>(playbackMachine, ":description_sheet")
    commentsSheet ?: SheetValue.Hidden
  }

  regSub("comments_sheet") { db: AppDb, _: Query ->
    val playbackFsm = db["stream_panel_fsm"]
    val playbackMachine = get<Any>(playbackFsm, fsm._state)
    val commentsSheet =
      get<SheetValue>(playbackMachine, ":comments_sheet")
    commentsSheet ?: SheetValue.Hidden
  }

  val loadingState = UIState(m(common.state to ListState.LOADING))

  regSub<IPersistentMap<Any, Any>, UIState>(
    queryId = Stream.comments,
    initialValue = loadingState,
    inputSignal = v("stream_panel_fsm")
  ) { panelFsm: IPersistentMap<Any, Any>?, prev, _ ->
    val fsmStates = get<Any>(panelFsm, fsm._state)
    val commentsListState =
      get(fsmStates, ":comments_list") ?: ListState.LOADING

    val commentsSheet = get(fsmStates, ":comments_sheet") ?: SheetValue.Hidden
    val commentsState: IPersistentMap<Any?, Any?> = when (commentsListState) {
      ListState.LOADING -> m()
      ListState.REFRESHING, ListState.APPENDING -> {
        prev.data as IPersistentMap<Any?, Any?>
      }

      ListState.READY -> {
        val sc = get<StreamComments>(panelFsm, "stream_comments")!!
        val stream = get<StreamData>(panelFsm, "stream_data")
        val comments = sc.comments.map { comment ->
          UIState(mapComment(comment, stream))
        }

        m(
          "comments_list" to comments,
          "comments_section" to highlightComment(sc),
          ":comments_sheet" to commentsSheet
        )
      }
    }

    UIState(data = commentsState.assoc(common.state, commentsListState))
  }

  regSub<IPersistentMap<Any, Any>, UIState?>(
    queryId = common.comment_replies,
    initialValue = null,
    inputSignal = v("stream_panel_fsm")
  ) { panelFsm: IPersistentMap<Any, Any>?, prev, _ ->
    val fsmStates = get<Any>(panelFsm, fsm._state)
    val commentRepliesState = get<ListState>(fsmStates, ":comment_replies")
      ?: return@regSub null

    val ret: IPersistentMap<Any?, Any?> = when (commentRepliesState) {
      ListState.LOADING -> m()

      ListState.READY -> {
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

      ListState.REFRESHING, ListState.APPENDING -> {
        prev!!.data as IPersistentMap<Any?, Any?>
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
      m("comments" to loadingState)
    ),
    v(Stream.comments),
    v(common.comment_replies)
  ) { (comments, replies), _, _ ->
    UIState(
      m("comments" to comments).let {
        if (replies != null) it.assoc("replies", replies) else it
      }
    )
  }
}

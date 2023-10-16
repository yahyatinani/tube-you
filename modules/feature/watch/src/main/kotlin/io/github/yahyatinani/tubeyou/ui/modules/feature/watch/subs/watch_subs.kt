package io.github.yahyatinani.tubeyou.ui.modules.feature.watch.subs

import android.content.Context
import android.content.res.Configuration
import android.text.SpannedString
import android.text.TextPaint
import android.text.style.URLSpan
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.PartiallyExpanded
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import io.github.yahyatinani.y.core.assoc
import io.github.yahyatinani.y.core.collections.Associative
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.collections.PersistentArrayMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.getIn
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.selectKeys
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

fun ratio(streamData: StreamData?): Float {
  val default = 16 / 9f
  if (streamData == null) return default
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

fun height(stream: StreamData) = stream.videoStreams.firstOrNull {
  val codec: String? = it.codec
  codec != null && codec.contains("avc1")
}?.height ?: 0

private val Delta = 18.dp

/**
 * e.g.: 2023-10-03
 */
val DateRegex = "\\d{2,4}-\\d{1,2}-\\d{1,2}".toRegex()

private fun year(uploadDate: String) = DateRegex.find(uploadDate)
  ?.groupValues
  ?.first()
  ?.toLocalDate()
  ?.year.toString()

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RegWatchSubs() {
  regSub("stream_panel_fsm") { db: AppDb, _: Query ->
    db["stream_panel_fsm"]
  }

  regSub("active_stream_vm") { db: AppDb, _: Query ->
    get<VideoVm>(db["stream_panel_fsm"], "active_stream")
      ?: VideoVm()
  }

  regSub(":stream_fsm") { db: AppDb, _: Query ->
    val streamPanelFsm = db["stream_panel_fsm"]
    selectKeys(
      streamPanelFsm,
      l(
        "stream_data",
        "current_quality",
        "quality_list",
        "active_stream",
        "show_player_thumbnail"
      )
    ).assoc(
      ":player_state",
      get<StreamState>(get<Any>(streamPanelFsm, fsm._state), ":player")
    )
  }

  regSub("is_player_sheet_minimized") { db: AppDb, _: Query ->
    get<SheetValue>(
      get<Any>(db["stream_panel_fsm"], fsm._state),
      ":player_sheet"
    ) == PartiallyExpanded
  }

  regSub<IPersistentMap<Any, Any>, UIState?>(
    queryId = ":now_playing_stream",
    initialValue = null,
    v(":stream_fsm")
  ) { streamFsm, _, (_, context, configuration, density) -> // (_, context, cfg, density)
    val playerRegionState = get<StreamState>(streamFsm, ":player_state")
      ?: return@regSub null

    val preLoadedStreamData = get<VideoVm>(streamFsm, "active_stream")!!
    val stream = get<StreamData>(streamFsm, "stream_data")
    val ratio = ratio(stream)

    val streamHeight = with(density as Density) {
      ((configuration as Configuration).screenWidthDp.dp.toPx() / ratio).toDp()
    }

    if (playerRegionState == StreamState.LOADING) {
      return@regSub UIState(
        m(
          common.is_loading to true,
          Stream.thumbnail to preLoadedStreamData.thumbnail,
          Stream.title to preLoadedStreamData.title,
          Stream.views to preLoadedStreamData.viewCount,
          "show_player_thumbnail" to true,
          "stream_height" to streamHeight
        )
      )
    }

    val currentQuality = streamFsm["current_quality"]
    val ql = get<List<Pair<String, Int>>>(streamFsm, "quality_list") ?: l()
    val qualityList = ql.map {
      when (currentQuality) {
        it.second -> "${it.first} ✓"
        else -> it.first
      } to it.second
    }

    val views = formatViews(stream!!.views).let {
      if (preLoadedStreamData.isLiveStream) {
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
    val showThumbnail =
      get<Boolean>(streamFsm, "show_player_thumbnail") ?: false

    val m = m(
      common.state to playerRegionState,
      Stream.title to stream.title,
      Stream.uploader to stream.uploader,
      Stream.thumbnail to preLoadedStreamData.thumbnail,
      Stream.quality_list to qualityList,
      Stream.current_quality to when (currentQuality) {
        null -> ""
        else -> "${currentQuality}p"
      },
      Stream.aspect_ratio to ratio,
      Stream.views to views,
      Stream.date to shortenTime(preLoadedStreamData.uploaded),
      Stream.channel_name to preLoadedStreamData.uploaderName,
      Stream.avatar to (stream.uploaderAvatar ?: ""),
      Stream.sub_count to formatSubCount(stream.uploaderSubscriberCount),
      Stream.likes_count to formatViews(stream.likes),
      Stream.views_full to formatFullViews(stream.views),
      Stream.year to year(uploadDate = stream.uploadDate),
      Stream.month_day to formatToMonthDay(stream.uploadDate),
      Stream.related_streams to relatedStreams,
      Stream.description to HtmlCompat.fromHtml(
        stream.description,
        HtmlCompat.FROM_HTML_MODE_LEGACY
      ),
      common.is_loading to false,
      "show_player_thumbnail" to showThumbnail,
      "stream_height" to streamHeight
    )

    UIState(
      data = if (stream.videoStreams.isNotEmpty()) {
        m.assoc(
          Stream.video_uri,
          when {
            stream.livestream && stream.dash != null -> stream.dash!!.toUri()
            else -> createDashSource(stream, context as Context)
          }
        ).assoc(Stream.mime_type, MimeTypes.APPLICATION_MPD)
      } else {
        m.assoc(Stream.video_uri, stream.hls!!.toUri())
          .assoc(Stream.mime_type, MimeTypes.APPLICATION_M3U8)
      }
    )
  }

  // description sheet:

  val context = LocalContext.current
  regSub<UIState?, Any?>(
    queryId = "description_state",
    initialValue = null,
    inputSignal = v(
      ":now_playing_stream",
      context,
      LocalConfiguration.current,
      LocalDensity.current
    )
  ) { input, _, _ ->
    if (input == null) return@regSub null

    val data = input.data
    val isLoading = get(data, common.is_loading, false)!!

    if (isLoading) {
      return@regSub UIState(
        m(
          Stream.title to "",
          Stream.likes_count to "",
          Stream.views_full to "",
          Stream.month_day to "",
          Stream.year to "",
          Stream.description to "",
          Stream.avatar to "",
          Stream.sub_count to "",
          Stream.channel_name to ""
        )
      )
    }

    UIState(
      data = selectKeys(
        data,
        l(
          Stream.title,
          Stream.likes_count,
          Stream.views_full,
          Stream.month_day,
          Stream.year,
          Stream.description,
          Stream.avatar,
          Stream.sub_count,
          Stream.channel_name
        )
      )
    )
  }

  regSub(queryId = "description_sheet_fsm_state") { db: AppDb, _: Query ->
    get(
      get<Any>(db["stream_panel_fsm"], fsm._state),
      ":description_sheet",
      SheetValue.Hidden
    )!!
  }

  val defaultSheetPeekHeight = defaultSheetPeekHeight()
  val initSheetState = UIState(
    m(
      ":sheet_peak_height" to defaultSheetPeekHeight,
      ":sheet_content_height" to 0.dp
    )
  )

  regSub(
    queryId = "description_sheet_state",
    initialValue = initSheetState,
    v("description_sheet_fsm_state"),
    v(":stream_fsm")
  ) { (descSheetState, streamFsm), prev, (_, cfg, density) ->
    val (w, h) = with(density as Density) {
      (cfg as Configuration).screenWidthDp.dp.toPx() to
        cfg.screenHeightDp.dp.toPx()
    }
    if (descSheetState == SheetValue.Hidden) {
      return@regSub UIState(
        m(
          ":sheet_peak_height" to get(
            prev.data,
            ":sheet_peak_height",
            defaultSheetPeekHeight
          ),
          ":sheet_content_height" to 0.dp
        )
      )
    }

    val stream = get<StreamData>(streamFsm, "stream_data")!!
    val sheetPeekHeight = with(density) { (h - (w / ratio(stream))).toDp() }
    UIState(
      m(
        ":desc_sheet_value" to descSheetState,
        ":is_desc_sheet_open" to (descSheetState != SheetValue.Hidden),
        ":sheet_peak_height" to sheetPeekHeight,
        ":sheet_content_height" to when (descSheetState) {
          PartiallyExpanded -> sheetPeekHeight - Delta
          else -> Dp.Unspecified
        }
      )
    )
  }

  // comments sheet:

  regSub(queryId = ":comments_stream_data") { db: AppDb, _: Query ->
    val streamPanelFsm = db["stream_panel_fsm"]
    val listState = get(
      get<Any>(streamPanelFsm, fsm._state),
      ":comments_list",
      ListState.LOADING
    )!!
    selectKeys(
      streamPanelFsm,
      l("stream_data", "stream_comments")
    ).assoc(":comments_list_fsm_state", listState)
  }

  regSub<Any>(
    queryId = ":comments_section",
    initialValue = ListState.LOADING,
    v(":comments_stream_data")
  ) { (streamData), currentValue, _ ->
    val commentsListState =
      get<ListState>(streamData, ":comments_list_fsm_state")
    when (commentsListState as ListState) {
      ListState.LOADING -> ListState.LOADING
      ListState.REFRESHING, ListState.APPENDING -> currentValue

      ListState.READY -> {
        highlightComment(get<StreamComments>(streamData, "stream_comments")!!)
      }
    }
  }

  regSub("comments_sheet_fsm_state") { db: AppDb, _: Query ->
    get(get<Any>(db["stream_panel_fsm"], fsm._state), ":comments_sheet")
  }

  regSub(
    queryId = "comments_sheet_state",
    initialValue = initSheetState,
    v("comments_sheet_fsm_state"),
    v(":stream_fsm")
  ) { (sheetState, streamFsm), prev, (_, cfg, density) ->
    if (sheetState == null) return@regSub initSheetState
    if (sheetState == SheetValue.Hidden) {
      return@regSub UIState(
        m(
          ":sheet_peak_height" to get(
            prev.data,
            ":sheet_peak_height",
            defaultSheetPeekHeight
          ),
          ":sheet_content_height" to 0.dp
        )
      )
    }

    val sheetPeekHeight = with(density as Density) {
      val w = (cfg as Configuration).screenWidthDp.dp.toPx()
      val h = cfg.screenHeightDp.dp.toPx()
      (h - (w / ratio(get<StreamData>(streamFsm, "stream_data")!!))).toDp()
    }
    val isSheetOpen = sheetState == SheetValue.Expanded ||
      sheetState == PartiallyExpanded
    val contentHeight = when (sheetState) {
      "Expanding", PartiallyExpanded -> sheetPeekHeight - Delta
      else -> Dp.Unspecified
    }
    UIState(
      m(
        ":sheet_value" to sheetState,
        ":is_sheet_open" to isSheetOpen,
        ":sheet_peak_height" to sheetPeekHeight,
        ":sheet_content_height" to contentHeight
      )
    )
  }

  regSub<IPersistentMap<Any, Any>?>(
    queryId = Stream.comments,
    initialValue = null,
    v(":comments_stream_data"),
    v("comments_sheet_fsm_state")
  ) { (stream, sheetValue), prev, _ ->
    val initCommentsList = m("is_loading" to true)

    if (sheetValue == "Expanding") return@regSub initCommentsList

    when (get<ListState>(stream, ":comments_list_fsm_state")!!) {
      ListState.LOADING -> initCommentsList
      ListState.REFRESHING -> prev!!.assoc("is_refreshing", true)
      ListState.APPENDING -> prev!!.assoc("is_appending", true)

      ListState.READY -> {
        val streamData = get<StreamData>(stream, "stream_data")
        m(
          "comments_list" to get<StreamComments>(stream, "stream_comments")!!
            .comments
            .map { comment -> UIState(mapComment(comment, streamData)) }
        )
      }
    }
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

  regSub<UIState?>(
    queryId = "comments_panel",
    initialValue = null,
    v(Stream.comments),
    v(common.comment_replies)
  ) { (comments, replies), _, _ ->
    if (replies != null) {
      return@regSub UIState(
        assoc(comments as Associative<Any, Any>?, "replies" to replies)
      )
    }

    if (comments == null) return@regSub null

    UIState(comments)
  }

  regSub(queryId = "active_comments_route") { db: AppDb, _: Query ->
    getIn(db, l("stream_panel_fsm", "comments_panel_route")) ?: COMMENTS_ROUTE
  }
}

@Composable
private fun defaultSheetPeekHeight(): Dp {
  val density = LocalDensity.current
  val configuration = LocalConfiguration.current
  return remember(density, configuration) {
    with(density) {
      val h = configuration.screenHeightDp.dp.toPx()
      val w = configuration.screenWidthDp.dp.toPx()
      (h - (w / (16 / 9f))).toDp()
    }
  }
}

package com.github.yahyatinani.tubeyou.modules.panel.common

import androidx.compose.runtime.Immutable
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.playbackMachine
import io.github.yahyatinani.recompose.cofx.Coeffects
import io.github.yahyatinani.recompose.cofx.injectCofx
import io.github.yahyatinani.recompose.fsm.trigger
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.httpfx.ktor
import io.github.yahyatinani.recompose.ids.recompose
import io.github.yahyatinani.recompose.pagingfx.Page
import io.github.yahyatinani.recompose.pagingfx.paging
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.net.URLEncoder

@Immutable
@Serializable
data class StreamDetails(
  val url: String,
  val format: String,
  val mimeType: String,
  val codec: String?,
  val quality: String,
  val videoOnly: Boolean,
  val contentLength: Long,
  val width: Int,
  val height: Int,
  val bitrate: Long,
  val initStart: Int? = null,
  val initEnd: Int? = null,
  val indexStart: Int? = null,
  val indexEnd: Int? = null,
  val fps: Int? = null,
  val audioTrackId: String? = null
)

@Immutable
@Serializable
data class StreamData(
  val title: String,
  val description: String,
  val uploadDate: String,
  val uploader: String,
  val uploaderUrl: String,
  val uploaderAvatar: String,
  val thumbnailUrl: String,
  val hls: String?,
  val dash: String?,
  val category: String,
  val uploaderVerified: Boolean,
  val duration: Long,
  val views: Long,
  val likes: Long,
  val dislikes: Long,
  val uploaderSubscriberCount: Long,
  val videoStreams: List<StreamDetails>,
  val audioStreams: List<StreamDetails>,
  val livestream: Boolean
)

@Serializable
data class StreamComment(
  val author: String,
  val thumbnail: String,
  val commentId: String,
  val commentText: String,
  val commentedTime: String,
  val commentorUrl: String,
  val repliesPage: String? = null,
  val likeCount: Long,
  val replyCount: Long,
  val hearted: Boolean,
  val pinned: Boolean,
  val verified: Boolean
)

@Immutable
@Serializable
data class StreamComments(
  val comments: List<StreamComment>,
  val nextpage: String? = null,
  val disabled: Boolean,
  val commentCount: Long
) : Page {

  @Transient
  override val data = comments

  @Transient
  override val nextKey: String? = if (nextpage != "null" && nextpage != null) {
    URLEncoder.encode(nextpage, "UTF-8")
  } else {
    null
  }

  @Transient
  override val prevKey: String? = null
}

fun regCommonEvents() {
  regEventFx(
    id = "load_stream",
    interceptors = v(injectCofx("player_scope"))
  ) { cofx: Coeffects, (_, videoId) ->
    val appDbBy = appDbBy(cofx)
    val appDb = appDbBy.dissoc("current_video_stream")
    val id = (videoId as String).replace("/watch?v=", "")
    m(
      fx to v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to "${appDb[common.api_url]}/streams/$id",
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx["player_scope"],
            ktor.response_type_info to typeInfo<StreamData>(),
            ktor.on_success to v("playback_fsm", "launch_stream"),
            ktor.on_failure to v("todo")
          )
        )
      )
    )
  }

  regEventFx(
    id = "load_stream_comments",
    interceptors = v(injectCofx("player_scope"))
  ) { cofx: Coeffects, (_, videoId) ->
    val id = (videoId as String).replace("/watch?v=", "")
    val api = appDbBy(cofx)[common.api_url]
    val commentsEvent = v("playback_fsm", "set_stream_comments")
    m(
      fx to v(
        v(
          paging.fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to "$api/comments/$id",
            ktor.timeout to 8000,
            "pageName" to "nextpage",
            "nextUrl" to "$api/nextpage/comments/$id",
            "eventId" to "load_stream",
            paging.append_id to "append_comments",
            ktor.coroutine_scope to cofx["player_scope"],
            ktor.response_type_info to typeInfo<StreamComments>(),
            ktor.on_success to commentsEvent,
            paging.on_page_success to v("playback_fsm", "append_comments_page"),
            "on_appending" to v("playback_fsm"),
            ktor.on_failure to v(""),
            paging.page_size to 5
          )
        )
      )
    )
  }

  regEventFx(
    id = "playback_fsm",
    interceptors = v(injectCofx(":screen_dimen_px"))
  ) { cofx, e ->
    println("playback_fsm $e")
    val value = cofx[":screen_dimen_px"]
    val appDb = appDbBy(cofx).let {
      if (value == null) it else it.assoc(":screen_dimen_px", value)
    }
    trigger(
      playbackMachine,
      m(recompose.db to appDb),
      v("playback_fsm"),
      e.subvec(1, e.count)
    )
  }
}

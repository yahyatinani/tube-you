package com.github.yahyatinani.tubeyou.modules.panel.common

import androidx.compose.runtime.Immutable
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.active_stream
import io.github.yahyatinani.recompose.cofx.Coeffects
import io.github.yahyatinani.recompose.cofx.injectCofx
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.httpfx.ktor
import io.github.yahyatinani.recompose.ids.recompose
import io.github.yahyatinani.recompose.regEventDb
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.y.core.assocIn
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.getIn
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class VideoStream(
  val url: String,
  val format: String,
  val mimeType: String,
  val codec: String?,
  val quality: String,
  val videoOnly: Boolean,
  val contentLength: Long,
  val width: Int,
  val height: Int
)

@Immutable
@Serializable
data class AudioStream(
  val url: String,
  val format: String,
  val quality: String,
  val mimeType: String,
  val codec: String,
  val bitrate: Long
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
  val category: String,
  val uploaderVerified: Boolean,
  val duration: Long,
  val views: Long,
  val likes: Long,
  val dislikes: Long,
  val uploaderSubscriberCount: Long,
  val videoStreams: List<VideoStream>,
  val audioStreams: List<AudioStream>,
  val livestream: Boolean
)

fun regCommonEvents() {
  regEventDb<AppDb>("set_current_stream") { db, (_, streamData) ->
    val isPlayerVisible: Boolean = get(db, "is_player_sheet_visible") ?: false
    if (!isPlayerVisible) return@regEventDb db

    val videos = (streamData as StreamData).videoStreams.filter { it.videoOnly }
    db.assoc(
      "current_video_stream",
      streamData.copy(videoStreams = videos)
    )
  }

  regEventDb<AppDb>("hidePlayerThumbnail") { db, _ ->
    val tmp = assocIn(db, l(active_stream, "show_player_thumbnail"), false)
    assocIn(tmp, l(active_stream, "show_player_loading"), false)
  }

  regEventDb<AppDb>("showPlayerThumbnail") { db, _ ->
    assocIn(db, l(active_stream, "show_player_thumbnail"), true)
  }

  regEventDb<AppDb>("showPlayerLoading") { db, _ ->
    assocIn(db, l(active_stream, "show_player_loading"), true)
  }

  regEventFx(
    id = "load_stream",
    interceptors = v(injectCofx("player_scope"))
  ) { cofx: Coeffects, _ ->
    val appDbBy = appDbBy(cofx)
    val videoId = getIn<Any>(appDbBy, l(active_stream, "videoId"))
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
            ktor.on_success to v("set_current_stream"),
            ktor.on_failure to v("todo")
          )
        )
      )
    )
  }

  regEventFx(common.play_video) { cofx: Coeffects, (_, videoId, thumbnail) ->
    val appDbBy = appDbBy(cofx)
    if (getIn<String>(appDbBy, l(active_stream, "videoId")) == videoId) {
      return@regEventFx m(fx to v(v(common.expand_player_sheet)))
    }
    val appDb = appDbBy.dissoc("current_video_stream")
    m(
      recompose.db to assocIn(appDb, l(active_stream, "videoId"), videoId)
        .assoc("current_video_thumbnail", thumbnail)
        .assoc("is_player_sheet_visible", true),
      fx to v(v(common.play_new_stream))
    )
  }

  regEventDb("is_player_sheet_visible") { db: AppDb, (_, flag) ->
    db.assoc("is_player_sheet_visible", flag)
  }
}

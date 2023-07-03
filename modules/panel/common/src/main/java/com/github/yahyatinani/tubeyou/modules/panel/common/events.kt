package com.github.yahyatinani.tubeyou.modules.panel.common

import androidx.compose.runtime.Immutable
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.cofx.Coeffects
import io.github.yahyatinani.recompose.cofx.injectCofx
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.httpfx.ktor
import io.github.yahyatinani.recompose.ids.recompose
import io.github.yahyatinani.recompose.regEventDb
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.y.core.get
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
  val height: Int,
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
  regEventDb<AppDb>("set_current_video_uri") { db, (_, streamData) ->
    val videos = (streamData as StreamData).videoStreams.filter { it.videoOnly }
    db.assoc("current_video_stream", streamData.copy(videoStreams = videos))
  }

  regEventFx(
    id = common.play_video,
    interceptors = v(injectCofx(common.coroutine_scope))
  ) { cofx: Coeffects, (_, videoId) ->
    val appDb = appDbBy(cofx)
    val id = (videoId as String).replace("/watch?v=", "")
    m(
      recompose.db to appDb
        .dissoc("current_video_stream")
        .assoc("is_player_sheet_visible", true),
      BuiltInFx.fx to v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to "${appDb[common.api_url]}/streams/$id",
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[common.coroutine_scope],
            ktor.response_type_info to typeInfo<StreamData>(),
            ktor.on_success to v("set_current_video_uri"),
            ktor.on_failure to v("todo")
          )
        ),
        v(common.play_new_stream)
      )
    )
  }

  regEventDb("is_player_sheet_visible") { db: AppDb, (_, flag) ->
    db.assoc("is_player_sheet_visible", flag)
  }
}

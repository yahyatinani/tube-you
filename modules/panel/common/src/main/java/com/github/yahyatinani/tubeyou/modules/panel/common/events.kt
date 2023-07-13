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
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.Serializable

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

  regEventFx(id = "playback_fsm") { cofx, e ->
    trigger(
      playbackMachine,
      m(recompose.db to appDbBy(cofx)),
      v("playback_fsm"),
      e.subvec(1, e.count)
    )
  }
}

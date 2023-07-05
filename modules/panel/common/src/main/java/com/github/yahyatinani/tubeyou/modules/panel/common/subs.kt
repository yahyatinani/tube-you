package com.github.yahyatinani.tubeyou.modules.panel.common

import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.recompose.subs.Query
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.getIn
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

enum class Stream {
  audio_uri,
  video_uri,
  aspect_ratio,
  thumbnail
}

fun regCommonSubs() {
  regSub(queryId = "current_video_stream", key = "current_video_stream")

  regSub(queryId = "current_video_thumbnail", key = "current_video_thumbnail")

  regSub<AppDb>(queryId = "show_player_thumbnail") { db, _ ->
    getIn(db, l(common.active_stream, "show_player_thumbnail"))
  }

  regSub<StreamData?, Any?>(
    queryId = "currently_playing",
    initialValue = null,
    inputSignal = v("current_video_stream")
  ) { streamData: StreamData?, _, _ ->
    if (streamData == null) return@regSub null

    val aUri = streamData.audioStreams.first().url
    val stream = streamData.videoStreams.first {
      val codec: String? = it.codec
      codec != null && codec.contains("avc1")
    }
    val w = stream.width / 240f
    val h = stream.height / 240f
    m(
      Stream.audio_uri to aUri,
      Stream.video_uri to stream.url,
      Stream.thumbnail to streamData.thumbnailUrl,
      Stream.aspect_ratio to w / h
    )
  }

  regSub("is_playing") { db: AppDb, _: Query ->
    db["is_playing"] ?: false
  }

  regSub("is_player_sheet_visible") { db: AppDb, _: Query ->
    db["is_player_sheet_visible"] ?: false
  }
}

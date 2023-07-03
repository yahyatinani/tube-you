package com.github.yahyatinani.tubeyou.modules.panel.common

import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.recompose.subs.Query
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

enum class Stream {
  audio_uri,
  video_uri,
  aspect_ratio
}

fun regCommonSubs() {
  regSub(queryId = "current_video_stream", key = "current_video_stream")

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

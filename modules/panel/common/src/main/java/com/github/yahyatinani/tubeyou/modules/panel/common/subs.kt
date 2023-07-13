package com.github.yahyatinani.tubeyou.modules.panel.common

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MimeTypes
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.PlayerState
import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.createDashSource
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.recompose.subs.Query
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

enum class Stream {
  title,
  uploader,
  video_uri,
  aspect_ratio,
  thumbnail,
  quality_list,
  current_quality,
  mime_type
}

private fun ratio(streamData: StreamData): Float {
  val stream = streamData.videoStreams.first {
    val codec: String? = it.codec
    codec != null && codec.contains("avc1")
  }
  val w = stream.width / 240f
  val h = stream.height / 240f
  return w / h
}

fun regCommonSubs() {
  regSub("playback_fsm") { db: AppDb, _: Query ->
    db["playback_fsm"]
  }

  regSub<IPersistentMap<Any, Any>, Any?>(
    queryId = "currently_playing",
    initialValue = null,
    inputSignal = v("playback_fsm")
  ) { playbackFsm: IPersistentMap<Any, Any>?, _, (_, context) ->
    val playbackMachine = get<Any>(playbackFsm, fsm._state)
    val playerRegion = get<Any>(playbackMachine, ":player")
    val stream = playbackFsm?.get("stream_data") as StreamData?

    if (playerRegion == null || playerRegion == PlayerState.LOADING ||
      playbackFsm == null ||
      stream == null
    ) {
      return@regSub null
    }

    println("playerRegion $playerRegion")

    val currentQuality = playbackFsm["current_quality"]
    val ql = get<List<Pair<String, Int>>>(playbackFsm, "quality_list") ?: l()
    val qualityList = ql.map {
      val a = if (currentQuality == it.second) "${it.first} ✓" else it.first
      a to it.second
    }

    val cq = if (currentQuality == null) "" else "${currentQuality}p"

    val m = m<Stream, Any?>(
      Stream.title to stream.title,
      Stream.uploader to stream.uploader,
      Stream.thumbnail to stream.thumbnailUrl,
      Stream.quality_list to qualityList,
      Stream.current_quality to cq,
      Stream.aspect_ratio to if (stream.livestream) 16 / 9f else ratio(stream)
    )

    if (stream.videoStreams.isNotEmpty()) {
      if (stream.livestream && stream.dash != null) {
        m.assoc(Stream.video_uri, stream.dash.toUri())
      } else {
        m.assoc(
          Stream.video_uri,
          createDashSource(stream, context as Context)
        )
      }.assoc(Stream.mime_type, MimeTypes.APPLICATION_MPD)
    } else {
      m.assoc(Stream.video_uri, stream.hls!!.toUri())
        .assoc(Stream.mime_type, MimeTypes.APPLICATION_M3U8)
    }
  }
}

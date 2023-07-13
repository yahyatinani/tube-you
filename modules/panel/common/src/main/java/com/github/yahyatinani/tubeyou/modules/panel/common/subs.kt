package com.github.yahyatinani.tubeyou.modules.panel.common

import com.github.yahyatinani.tubeyou.modules.panel.common.videoplayer.PlayerState
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
  current_quality
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
  ) { playbackFsm: IPersistentMap<Any, Any>?, _, _ ->
    val playbackMachine = get<Any>(playbackFsm, fsm._state)
    val playerRegion = get<Any>(playbackMachine, ":player")
    val streamData = playbackFsm?.get("stream_data") as StreamData?

    if (playerRegion == null || playerRegion == PlayerState.LOADING ||
      playbackFsm == null ||
      streamData == null
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

    val ratio = if (streamData.livestream) 16 / 9f else ratio(streamData)

    m(
      Stream.title to streamData.title,
      Stream.uploader to streamData.uploader,
      Stream.video_uri to streamData.hls,
      Stream.thumbnail to streamData.thumbnailUrl,
      Stream.quality_list to qualityList,
      Stream.current_quality to cq,
      Stream.aspect_ratio to ratio
    )
  }
}

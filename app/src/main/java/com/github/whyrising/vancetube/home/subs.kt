package com.github.whyrising.vancetube.home

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.vancetube.base.AppDb
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.map
import com.github.whyrising.y.core.v
import kotlinx.datetime.LocalTime
import java.math.RoundingMode
import java.text.DecimalFormat

fun homePanel(db: AppDb) = (db[home.panel] as AppDb)

fun popularVideos(db: AppDb): PersistentVector<VideoMetadata> =
  homePanel(db)[home.popular_vids] as PersistentVector<VideoMetadata>? ?: v()

fun formatSeconds(seconds: Int): String {
  val format = "%02d"
  val localTime = LocalTime.fromSecondOfDay(seconds)
  val s = format.format(localTime.second)
  return when (localTime.hour) {
    0 -> "${localTime.minute}:$s"
    else -> {
      val m = format.format(localTime.minute)
      "${localTime.hour}:$m:$s"
    }
  }
}

const val VIDEO_INFO_DIVIDER = " • "

fun formatVideoInfo(
  author: String,
  viewCount: String,
  publishedText: String
): AnnotatedString = buildAnnotatedString {
  append(author)
  append(VIDEO_INFO_DIVIDER)
  append(viewCount)
  append(" views") // TODO: locale this
  append(VIDEO_INFO_DIVIDER)
  append(publishedText)
}

val OneDigitDecimalFormat = DecimalFormat("#.#").apply {
  roundingMode = RoundingMode.FLOOR
}
const val thousandsSign = "K"
const val MillionsSign = "M"
const val BillionsSign = "B"

fun formatViews(viewsCount: Long): String = when {
  viewsCount < 1000 -> "$viewsCount"
  viewsCount < 10_000 -> {
    val x = viewsCount / 1000f
    "${OneDigitDecimalFormat.format(x)}$thousandsSign".replace(".0", "")
  }
  viewsCount < 100_000 -> "${viewsCount / 1000}$thousandsSign"
  viewsCount < 1_000_000 -> "${viewsCount / 1000}$thousandsSign"
  viewsCount < 1_000_000_000 -> "${viewsCount / 1_000_000}$MillionsSign"
  else -> "${viewsCount / 1_000_000_000}$BillionsSign"
}

enum class VideoIds {
  thumbnail,
  title,
  length,
  info
}

fun regHomeSubs() {
  regSub<AppDb, Any>(home.popular_vids) { db, _ ->
    popularVideos(db)
  }
  regSub<AppDb, Any>(home.video_item_height) { db, _ ->
    homePanel(db)[home.video_item_height] ?: 180
  }
  regSub<PersistentVector<VideoMetadata>, Any>(
    queryId = home.popular_vids_formatted,
    signalsFn = { subscribe(v(home.popular_vids)) },
    computationFn = { vids, _ ->
      map<VideoMetadata, Any>(vids) { videoMetadata ->
        val length = formatSeconds(videoMetadata.lengthSeconds)
        val info = formatVideoInfo(
          videoMetadata.author,
          formatViews(videoMetadata.viewCount),
          videoMetadata.publishedText
        )
        m(
          VideoIds.thumbnail to videoMetadata.videoThumbnails[1].url,
          VideoIds.title to videoMetadata.title,
          VideoIds.length to length,
          VideoIds.info to info
        )
      }
    }
  )
}

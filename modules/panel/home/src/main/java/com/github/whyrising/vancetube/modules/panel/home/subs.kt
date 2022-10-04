package com.github.whyrising.vancetube.modules.panel.home

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.home.popular_vids
import com.github.whyrising.vancetube.modules.designsystem.theme.Blue300
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getFrom
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.v
import kotlinx.datetime.LocalTime
import java.math.RoundingMode
import java.text.DecimalFormat

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
  authorId: String,
  viewCount: String,
  viewsLabel: String,
  publishedText: String
): AnnotatedString = buildAnnotatedString {
  "%#x ".format(3)
  val str = "$author$VIDEO_INFO_DIVIDER$viewCount $viewsLabel" +
    "$VIDEO_INFO_DIVIDER$publishedText"
  val startIndex = 0
  val endIndex = startIndex + author.length
  append(str)
  addStyle(
    style = SpanStyle(color = Blue300),
    start = startIndex,
    end = endIndex
  )
  addStringAnnotation(
    tag = "author",
    annotation = authorId,
    start = startIndex,
    end = endIndex
  )
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

data class VideoViewModel(
  val id: String,
  val authorId: String,
  val title: String,
  val thumbnail: String,
  val length: String,
  val info: AnnotatedString
)

fun toVms(
  videoDataList: List<VideoData>,
  viewsLabel: Any
): List<VideoViewModel> = videoDataList.fold(v()) { acc, videoMetadata ->
  acc.conj(
    VideoViewModel(
      id = videoMetadata.videoId,
      authorId = videoMetadata.authorId,
      title = videoMetadata.title,
      thumbnail = videoMetadata.videoThumbnails[4].url,
      length = formatSeconds(videoMetadata.lengthSeconds),
      info = formatVideoInfo(
        author = videoMetadata.author,
        authorId = videoMetadata.authorId,
        viewCount = formatViews(videoMetadata.viewCount),
        viewsLabel = viewsLabel as String,
        publishedText = videoMetadata.publishedText
      )
    )
  )
}

@Stable
data class PopularVideos(val value: List<VideoViewModel>)

data class HomeViewModel(
  val isLoading: Boolean = false,
  val isRefreshing: Boolean = false,
  val showList: Boolean = false,
  val popularVideos: PopularVideos = default
) {
  companion object {
    val default = PopularVideos(l())
  }
}

// -- Subs ---------------------------------------------------------------------

/**
 * Call this lazy global property to initialise all [Home] page subscriptions.
 * @return [Unit]
 */
val regHomeSubs by lazy {
  regSub<IPersistentMap<Any, Any>, Any?>(queryId = home.state) { db, _ ->
    db[home.panel]
  }

  regSub<IPersistentMap<Any, Any>, HomeViewModel>(
    queryId = home.view_model,
    signalsFn = { subscribe(v(home.state)) },
    computationFn = { homeDb, (_, viewsLabel) ->
      when (homeDb[home.state]) {
        States.Loading -> HomeViewModel(isLoading = true)
        States.Loaded -> {
          val videoDataList: List<VideoData> = getFrom(homeDb, popular_vids)!!
          val formatted = toVms(videoDataList, viewsLabel)
          HomeViewModel(
            showList = true,
            popularVideos = PopularVideos(formatted)
          )
        }
        else -> {
          // TODO: use previous computation to save CPU cycles.
          val videoDataList: List<VideoData> = getFrom(homeDb, popular_vids)!!
          val formatted = toVms(videoDataList, viewsLabel)
          HomeViewModel(
            isRefreshing = true,
            showList = true,
            popularVideos = PopularVideos(formatted)
          )
        }
      }
    }
  )
}

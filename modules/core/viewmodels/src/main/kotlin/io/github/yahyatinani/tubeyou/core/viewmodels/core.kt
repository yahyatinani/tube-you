package io.github.yahyatinani.tubeyou.core.viewmodels

import android.content.res.Resources
import android.text.format.DateFormat
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.Blue400
import io.github.yahyatinani.tubeyou.modules.core.network.Channel
import io.github.yahyatinani.tubeyou.modules.core.network.Playlist
import io.github.yahyatinani.tubeyou.modules.core.network.Video
import io.github.yahyatinani.tubeyou.modules.core.viewmodels.R
import io.github.yahyatinani.y.core.v
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun formatSeconds(durationInSeconds: Long): String {
  val hours = TimeUnit.SECONDS.toHours(durationInSeconds)
  val minutes = TimeUnit.SECONDS.toMinutes(durationInSeconds) % 60
  val seconds = durationInSeconds % 60
  return when {
    hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
    else -> String.format("%d:%02d", minutes, seconds)
  }
}

const val SMALL_BULLET = "·"
const val MEDIUM_BULLET = "•"

fun formatVideoInfo(
  author: String,
  authorId: String,
  text1: String,
  text2: String,
  publishedText: String? = null
): AnnotatedString = buildAnnotatedString {
  append("\u200F$author\u200E $SMALL_BULLET $text1 $text2")

  if (publishedText != null) {
    append(" $SMALL_BULLET $publishedText")
  }

  val endIndex = author.length + 1
  addStyle(
    style = SpanStyle(color = Blue400),
    start = 0,
    end = endIndex
  )
  addStringAnnotation(
    tag = "author annotation",
    annotation = authorId,
    start = 0,
    end = endIndex
  )
}

fun convertTimestamp(timestampSeconds: Long): String {
  val date = Date(timestampSeconds)
  return DateFormat.format("M/d/yy, h:mm a", date).toString()
}

val OneDigitDecimalFormat = DecimalFormat("#.#").apply {
  roundingMode =
    RoundingMode.FLOOR
}
const val ThousandsSign = "K"
const val MillionsSign = "M"
const val BillionsSign = "B"

fun formatViews(viewsCount: Long): String = when {
  viewsCount < 1000 -> "$viewsCount"
  viewsCount < 10_000 -> {
    val x = viewsCount / 1000f
    "${OneDigitDecimalFormat.format(x)}$ThousandsSign".replace(".0", "")
  }

  viewsCount < 1_000_000 -> "${viewsCount / 1000}$ThousandsSign"
  viewsCount < 10_000_000 -> {
    val x = viewsCount / 1_000_000f
    "${OneDigitDecimalFormat.format(x)}$MillionsSign".replace(".0", "")
  }

  viewsCount < 1_000_000_000 -> "${viewsCount / 1_000_000}$MillionsSign"

  viewsCount < 10_000_000_000 -> {
    val x = viewsCount / 1_000_000_000f
    "${OneDigitDecimalFormat.format(x)}$BillionsSign".replace(".0", "")
  }

  else -> "${viewsCount / 1_000_000_000}$BillionsSign"
}

/*val ranges = listOf(
  Pair(1E3, "K"),
  Pair(1E6, "M"),
  Pair(1E9, "B")
)*/

// TODO: Refactor
fun formatSubCount(subCount: Long): String = when {
  subCount == 0L -> ""
  subCount < 1000 -> "$subCount"
  subCount < 10_000 -> {
    val x = subCount / 1000f
    "${String.format("%.1f", x)}$ThousandsSign".replace(".0", "")
  }

  subCount < 1_000_000 -> "${subCount / 1000}$ThousandsSign"
  subCount < 100_000_000 -> {
    val x = subCount / 1_000_000f

    "${String.format("%.1f", x)}$MillionsSign".replace(".0", "")
  }

  subCount < 1_000_000_000 -> "${subCount / 1_000_000}$MillionsSign"

  subCount < 10_000_000_000 -> {
    val x = subCount / 1_000_000_000f
    "${String.format("%.1f", x)}$BillionsSign".replace(".0", "")
  }

  else -> "${subCount / 1_000_000_000}$BillionsSign"
}

@OptIn(ExperimentalContracts::class)
inline fun <T> T.letIf(b: Boolean, block: (T) -> T): T {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return if (b) block(this) else this
}

/**
 * Checkout https://github.com/TeamNewPipe/NewPipeExtractor/pull/268
 *
 * default.jpg 8.73 kb
 * mqdefault.jpg 39.76kb
 * hqdefault.jpg 76.84 kb
 * sddefault.jpg 121.76 kb
 * hq720.jpg 306.98 kb
 * maxresdefault.jpg 306.98 kb
 *
 */
private fun highQuality(thumbnail: String) =
  thumbnail.replace("hqdefault.jpg", "sddefault.jpg")

fun formatVideo(video: Video, resources: Resources): VideoVm {
  val isUpcoming = video.views == -1L && video.duration == -1L
  val authorId = video.uploaderUrl!!
  val isLiveStream = video.duration == -1L
  val uploaded = video.uploadedDate
  val uploaderName = video.uploaderName!!

  val vm = VideoVm(
    id = video.url,
    authorId = authorId,
    uploaderName = uploaderName,
    title = video.title,
    thumbnail = highQuality(video.thumbnail),
    length = formatSeconds(video.duration),
    uploaded = uploaded ?: "",
    uploaderAvatar = video.uploaderAvatar,
    isUpcoming = isUpcoming,
    isLiveStream = isLiveStream,
    isShort = video.isShort
  )

  return if (isUpcoming) {
    vm.copy(
      info = formatVideoInfo(
        author = uploaderName,
        authorId = authorId,
        text1 = resources.getString(R.string.scheduled_for),
        text2 = convertTimestamp(video.uploaded)
      )
    )
  } else {
    val viewCount = formatViews(video.views!!)
    when {
      isLiveStream -> {
        val label = resources.getString(R.string.watching)
        vm.copy(
          info = formatVideoInfo(
            author = uploaderName,
            authorId = authorId,
            text1 = viewCount,
            text2 = label
          ),
          viewCount = "$viewCount $label"
        )
      }

      else -> {
        val label = resources.getString(R.string.views_label)
        vm.copy(
          info = formatVideoInfo(
            author = uploaderName,
            authorId = authorId,
            text1 = viewCount,
            text2 = label,
            publishedText = video.uploadedDate
          ),
          viewCount = "$viewCount $label"
        )
      }
    }
  }
}

fun formatVideos(
  videoDataList: List<Video>,
  resources: Resources
): List<VideoVm> = videoDataList.fold(v()) { acc, video ->
  acc.conj(formatVideo(video, resources))
}

fun formatChannel(channel: Channel) = ChannelVm(
  id = channel.url,
  author = channel.name,
  subCount = formatSubCount(channel.subscribers.toLong()),
  handle = "@${channel.name.replace(" ", "")}",
  avatar = channel.thumbnail
)

fun formatPlayList(r: Playlist) = PlaylistVm(
  title = r.name,
  author = r.uploaderName,
  authorUrl = r.uploaderUrl,
  playlistId = r.url,
  thumbnailUrl = highQuality(r.thumbnail),
  videoCount = "${r.videos}"
)

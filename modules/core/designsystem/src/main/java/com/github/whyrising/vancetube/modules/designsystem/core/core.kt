package com.github.whyrising.vancetube.modules.designsystem.core

import android.text.format.DateFormat
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.github.whyrising.vancetube.modules.designsystem.theme.Blue300
import java.math.RoundingMode.FLOOR
import java.text.DecimalFormat
import java.util.Date
import java.util.concurrent.TimeUnit

fun formatSeconds(durationInSeconds: Long): String {
  val hours = TimeUnit.SECONDS.toHours(durationInSeconds)
  val minutes = TimeUnit.SECONDS.toMinutes(durationInSeconds) % 60
  val seconds = durationInSeconds % 60
  return when {
    hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
    else -> String.format("%d:%02d", minutes, seconds)
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

fun convertTimestamp(timestampMillis: Long): String {
  val date = Date(timestampMillis * 1000)
  return DateFormat.format("M/d/yy, h:mm a", date).toString()
}

/**
 * eg.: SNY • Scheduled for 4/24/23, 5:00 PM
 */
fun formatUpcomingInfo(
  author: String,
  premiereTimestamp: Long
): AnnotatedString = buildAnnotatedString {
  append(author)
  append(VIDEO_INFO_DIVIDER)
  append("Scheduled for ")
  append(convertTimestamp(premiereTimestamp))
}

val OneDigitDecimalFormat = DecimalFormat("#.#").apply { roundingMode = FLOOR }
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

val ranges = listOf(
  Pair(1E3, "K"),
  Pair(1E6, "M"),
  Pair(1E9, "B")
)

// TODO: Refactor
fun formatSubCount(subCount: Long): String = when {
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

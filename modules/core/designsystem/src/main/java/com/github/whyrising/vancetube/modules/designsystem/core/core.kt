package com.github.whyrising.vancetube.modules.designsystem.core

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.github.whyrising.vancetube.modules.designsystem.theme.Blue300
import java.math.RoundingMode.FLOOR
import java.text.DecimalFormat
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

val OneDigitDecimalFormat = DecimalFormat("#.#").apply { roundingMode = FLOOR }
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

fun formatSubCount(subCount: Long): String {
  val millionsSign = "M"
  val billionsSign = "B"

  return when {
    subCount < 1000 -> subCount.toString()
    subCount < 1_000_000 -> "${subCount / 1000}K"
    subCount < 1_000_000_000 -> {
      val x = subCount / 1_000_000f
      if (x == x.toInt().toFloat()) {
        "${x.toInt()}$millionsSign"
      } else {
        "${String.format("%.1f", x)}$millionsSign"
      }
    }

    else -> {
      val x = subCount / 1_000_000_000f
      if (x == x.toInt().toFloat()) {
        "${x.toInt()}$billionsSign"
      } else {
        "${String.format("%.1f", x)}$billionsSign"
      }
    }
  }
}

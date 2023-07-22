package com.github.yahyatinani.tubeyou.modules.designsystem.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yahyatinani.tubeyou.modules.designsystem.R
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.designsystem.theme.TyTheme

val roundedCornerShape = RoundedCornerShape(4.dp)

@Composable
fun DurationText(
  text: String,
  modifier: Modifier = Modifier,
  fontSize: TextUnit = 12.sp
) {
  Text(
    text = text,
    modifier = modifier,
    textAlign = TextAlign.Center,
    style = MaterialTheme.typography.bodySmall.copy(
      color = Color.White,
      fontSize = fontSize,
      fontWeight = FontWeight.Medium,
      platformStyle = PlatformTextStyle(includeFontPadding = false)
    )
  )
}

fun Modifier.bgModifier(backgroundColor: Color): Modifier = this
  .padding(bottom = 8.dp, end = 8.dp)
  .background(color = backgroundColor, shape = roundedCornerShape)
  .padding(horizontal = 4.dp)

@Composable
private fun IconText(
  backgroundColor: Color,
  icon: ImageVector,
  durationText: String
) {
  Row(
    modifier = Modifier.bgModifier(backgroundColor),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = "",
      modifier = Modifier.size(12.dp),
      tint = Color.White
    )

    Spacer(modifier = Modifier.width(2.dp))

    DurationText(text = durationText)
  }
}

@Composable
fun LiveDurationText() {
  IconText(
    backgroundColor = Color.Red,
    icon = Icons.Default.Podcasts,
    durationText = stringResource(R.string.duration_live)
  )
}

@Composable
fun ShortDurationText() {
  IconText(
    backgroundColor = Color.Black.copy(alpha = .8f),
    icon = Icons.Default.FlashOn,
    durationText = stringResource(R.string.duration_shorts)
  )
}

@Composable
fun VideoDurationText(duration: String, modifier: Modifier = Modifier) {
  DurationText(
    text = duration,
    modifier = modifier.bgModifier(
      backgroundColor = Color.Black.copy(alpha = .8f)
    )
  )
}

@Composable
fun ThumbnailContent(viewModel: VideoViewModel) = when {
  viewModel.isLiveStream -> LiveDurationText()
  viewModel.isShort -> ShortDurationText()
  else -> VideoDurationText(
    duration = when {
      viewModel.isUpcoming -> stringResource(R.string.upcoming)
      else -> viewModel.length
    }
  )
}

@Composable
fun VideoItemTitle(modifier: Modifier = Modifier, title: String) {
  Text(
    text = title,
    modifier = modifier,
    maxLines = 2,
    softWrap = true,
    overflow = TextOverflow.Ellipsis,
    style = MaterialTheme.typography.titleSmall.copy(lineHeight = 18.sp)
  )
}

@Composable
fun VideoItemInfo(
  modifier: Modifier = Modifier,
  info: AnnotatedString,
  textStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp)
) {
  val context = LocalContext.current
  val color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f)
  ClickableText(
    text = info,
    modifier = modifier,
    style = textStyle.copy(color = color),
    onClick = {
      info
        .getStringAnnotations("author", it, it)
        .firstOrNull()?.let { stringAnnotation ->
          // TODO: visit the channel of this video by id in stringAnnotation.
          Toast.makeText(
            context,
            "TODO: not implemented yet",
            Toast.LENGTH_SHORT
          ).show()
        }
    }
  )
}

@Composable
fun CountText(subscribersCount: String) {
  Text(
    text = subscribersCount,
    style = MaterialTheme.typography.bodySmall.copy(
      color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f)
    )
  )
}

@Composable
fun ExpandableText(
  text: String,
  modifier: Modifier = Modifier,
  fontSize: TextUnit = TextUnit.Unspecified,
  fontStyle: FontStyle? = null,
  minimizedMaxLines: Int = 1,
  style: TextStyle = LocalTextStyle.current
) {
  var cutText by remember(text) { mutableStateOf<String?>(null) }
  var expanded by remember { mutableStateOf(false) }
  val textLayoutResultState =
    remember { mutableStateOf<TextLayoutResult?>(null) }
  val seeMoreSizeState = remember { mutableStateOf<IntSize?>(null) }
  val seeMoreOffsetState = remember { mutableStateOf<Offset?>(null) }

  // getting raw values for smart cast
  val textLayoutResult = textLayoutResultState.value
  val seeMoreSize = seeMoreSizeState.value
  val seeMoreOffset = seeMoreOffsetState.value

  LaunchedEffect(text, expanded, textLayoutResult, seeMoreSize) {
    val lastLineIndex = minimizedMaxLines - 1
    if (!expanded && textLayoutResult != null && seeMoreSize != null &&
      lastLineIndex + 1 == textLayoutResult.lineCount &&
      textLayoutResult.isLineEllipsized(lastLineIndex)
    ) {
      var lastCharIndex =
        textLayoutResult.getLineEnd(lastLineIndex, visibleEnd = true) + 1
      var charRect: Rect
      do {
        lastCharIndex -= 1
        charRect = textLayoutResult.getCursorRect(lastCharIndex)
      } while (
        charRect.left > textLayoutResult.size.width - seeMoreSize.width
      )
      seeMoreOffsetState.value =
        Offset(charRect.left, charRect.bottom - seeMoreSize.height)
      cutText = text.substring(startIndex = 0, endIndex = lastCharIndex)
    }
  }

  Box(modifier) {
    Text(
      text = cutText ?: text,
      maxLines = if (expanded) Int.MAX_VALUE else minimizedMaxLines,
      overflow = TextOverflow.Ellipsis,
      fontSize = fontSize,
      fontStyle = fontStyle,
      onTextLayout = { textLayoutResultState.value = it },
      style = style
    )
    if (!expanded) {
      val density = LocalDensity.current
      val labelLarge = MaterialTheme.typography.bodyMedium
      val string = buildAnnotatedString {
        withStyle(labelLarge.toSpanStyle()) {
          append("... ")
        }
        withStyle(
          labelLarge.copy(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f)
          ).toSpanStyle()
        ) {
          append("Read more")
        }
      }
      Text(
        string,
        onTextLayout = { seeMoreSizeState.value = it.size },
        modifier = Modifier
          .then(
            if (seeMoreOffset != null) {
              Modifier.offset(
                x = with(density) { seeMoreOffset.x.toDp() },
                y = with(density) { seeMoreOffset.y.toDp() }
              )
            } else {
              Modifier
            }
          )
          .clickable {
            expanded = true
            cutText = null
          }
          .alpha(if (seeMoreOffset != null) 1f else 0f)
      )
    }
  }
}

// -- Previews -----------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun VideoLengthTextPreview() {
  TyTheme {
    VideoDurationText(duration = "2:23")
  }
}

@Preview(showBackground = true)
@Composable
fun LiveStreamTextPreview() {
  TyTheme {
    LiveDurationText()
  }
}

@Preview(showBackground = true)
@Composable
fun ShortVideoTextPreview() {
  TyTheme {
    ShortDurationText()
  }
}

@Preview(showBackground = true)
@Composable
fun VideoItemInfoPreview() {
  TyTheme {
    VideoItemInfo(info = AnnotatedString("author سلاام . 2K"))
  }
}

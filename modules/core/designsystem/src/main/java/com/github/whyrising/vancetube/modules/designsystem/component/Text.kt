package com.github.whyrising.vancetube.modules.designsystem.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.whyrising.vancetube.modules.designsystem.R
import com.github.whyrising.vancetube.modules.designsystem.theme.VanceTheme

private val roundedCornerShape = RoundedCornerShape(4.dp)

@Composable
private fun DurationText(
  text: String,
  modifier: Modifier = Modifier
) {
  Text(
    text = text,
    modifier = modifier,
    textAlign = TextAlign.Center,
    style = MaterialTheme.typography.bodySmall.copy(
      color = Color.White,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      platformStyle = PlatformTextStyle(includeFontPadding = false)
    )
  )
}

private fun Modifier.bgModifier(backgroundColor: Color) = this
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
fun VideoDurationText(
  duration: String,
  modifier: Modifier = Modifier
) {
  val backgroundColor = Color.Black.copy(alpha = .8f)
  DurationText(
    text = duration,
    modifier = modifier.bgModifier(backgroundColor)
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
    style = MaterialTheme.typography.titleSmall
  )
}

@Composable
fun VideoItemInfo(
  modifier: Modifier = Modifier,
  info: AnnotatedString,
  textStyle: TextStyle = TextStyle.Default
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

// -- Previews -----------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun VideoLengthTextPreview() {
  VanceTheme {
    VideoDurationText(duration = "2:23")
  }
}

@Preview(showBackground = true)
@Composable
fun LiveStreamTextPreview() {
  VanceTheme {
    LiveDurationText()
  }
}

@Preview(showBackground = true)
@Composable
fun ShortVideoTextPreview() {
  VanceTheme {
    ShortDurationText()
  }
}

@Preview(showBackground = true)
@Composable
fun VideoItemInfoPreview() {
  VanceTheme {
    VideoItemInfo(info = AnnotatedString("author . 2K"))
  }
}

package com.github.whyrising.vancetube.modules.designsystem.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.whyrising.vancetube.modules.designsystem.theme.VanceTheme

@Composable
fun VideoLengthText(
  videoLength: String,
  modifier: Modifier = Modifier
) {
  Text(
    modifier = modifier
      .padding(8.dp)
      .background(
        color = Color.Black.copy(alpha = .8f),
        shape = RoundedCornerShape(2.dp)
      )
      .padding(horizontal = 3.dp),
    text = videoLength,
    textAlign = TextAlign.Center,
    style = MaterialTheme.typography.bodySmall.copy(
      color = Color.White,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      platformStyle = PlatformTextStyle(includeFontPadding = false)
    )
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
    VideoLengthText(videoLength = "2:23")
  }
}

@Preview(showBackground = true)
@Composable
fun VideoItemInfoPreview() {
  VanceTheme {
    VideoItemInfo(info = AnnotatedString("author . 2K"))
  }
}

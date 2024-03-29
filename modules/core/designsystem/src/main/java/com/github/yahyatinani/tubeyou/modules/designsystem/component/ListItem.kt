package com.github.yahyatinani.tubeyou.modules.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons

@Composable
fun ListItemPortrait(
  title: String,
  modifier: Modifier = Modifier,
  thumbnail: String,
  info: AnnotatedString,
  videoInfoTextStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
  thumbnailContent: @Composable (BoxScope.() -> Unit),
  channelAvatar: @Composable (RowScope.() -> Unit)? = null,
  onClick: () -> Unit = {}
) {
  Column(modifier = Modifier.clickable(onClick = onClick)) {
    Thumbnail(
      url = thumbnail,
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(RATIO),
      content = thumbnailContent
    )

    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(start = 8.dp, end = 4.dp, bottom = 24.dp),
      verticalAlignment = Alignment.Top
    ) {
      channelAvatar?.let { it() }
      Column(modifier = Modifier.weight(1f)) {
        VideoItemTitle(title = title)
        Spacer(modifier = Modifier.height(4.dp))
        VideoInfoText(info = info, textStyle = videoInfoTextStyle)
      }

      Spacer(modifier = Modifier.width(24.dp))

      MoreButton()
    }
  }
}

@Composable
fun ListItemLandscape(
  title: String,
  modifier: Modifier = Modifier,
  thumbnail: String,
  info: AnnotatedString,
  channelAvatar: @Composable (ColumnScope.() -> Unit)? = null,
  onClick: () -> Unit,
  content: @Composable (BoxScope.() -> Unit)
) {
  Row(modifier = modifier.clickable(onClick = onClick)) {
    Thumbnail(
      url = thumbnail,
      modifier = Modifier
        .weight(.25f)
        .aspectRatio(RATIO)
        .clip(RoundedCornerShape(8.dp)),
      content = content
    )

    Spacer(modifier = Modifier.width(16.dp))

    Column(modifier = Modifier.weight(.8f)) {
      VideoItemTitle(title = title)

      Spacer(modifier = Modifier.height(2.dp))

      VideoInfoText(info = info)

      channelAvatar?.let { it() }
    }

    MoreButton()
  }
}

@Composable
fun SearchSuggestionItem(text: String, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(50.dp)
      .clickable(onClick = onClick),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = TyIcons.Search,
      contentDescription = "Suggestion icon",
      modifier = Modifier
        .size(28.dp)
        .weight(weight = .1f)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
      text = text,
      modifier = Modifier.weight(weight = 1f),
      style = LocalTextStyle.current.copy(lineHeight = 18.sp)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Icon(
      imageVector = TyIcons.ArrowOutward,
      contentDescription = "Suggestion icon",
      modifier = Modifier
        .size(28.dp)
        .weight(weight = .1f)
    )
  }
}

const val RATIO = 16 / 9f

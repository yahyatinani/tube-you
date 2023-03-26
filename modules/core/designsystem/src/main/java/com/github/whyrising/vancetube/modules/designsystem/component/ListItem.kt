package com.github.whyrising.vancetube.modules.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.whyrising.vancetube.modules.designsystem.data.VideoViewModel

@Composable
fun VideoItemPortrait(
  modifier: Modifier = Modifier,
  videoInfoTextStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
  viewModel: VideoViewModel
) {
  Column(modifier = Modifier.clickable { /*todo:*/ }) {
    Thumbnail(
      modifier = Modifier.fillMaxWidth(),
      url = viewModel.thumbnail,
      videoLength = viewModel.length
    )
    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(top = 8.dp, end = 4.dp, bottom = 24.dp)
    ) {
      Column(modifier = Modifier.weight(1f)) {
        VideoItemTitle(title = viewModel.title)
        Spacer(modifier = Modifier.height(4.dp))
        VideoItemInfo(
          info = viewModel.info,
          textStyle = videoInfoTextStyle
        )
      }

      Spacer(modifier = Modifier.width(24.dp))

      VideoItemMoreButton()
    }
  }
}

@Composable
fun VideoListItemLandscapeCompact(viewModel: VideoViewModel) {
  Row(
    modifier = Modifier
      .testTag("video")
      .padding(vertical = 8.dp)
      .clickable { /*todo:*/ }
  ) {
    Thumbnail(
      modifier = Modifier.weight(.24f),
      url = viewModel.thumbnail,
      videoLength = viewModel.length
    )

    Spacer(modifier = Modifier.width(16.dp))

    Column(modifier = Modifier.weight(.8f)) {
      VideoItemTitle(title = viewModel.title)
      Spacer(modifier = Modifier.height(4.dp))
      VideoItemInfo(info = viewModel.info)
    }

    VideoItemMoreButton()
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
      imageVector = Icons.Default.Search,
      contentDescription = "Suggestion icon",
      modifier = Modifier.weight(weight = .1f)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
      text = text,
      modifier = Modifier.weight(weight = 1f),
      style = LocalTextStyle.current.copy(lineHeight = 18.sp)
    )
    Spacer(modifier = Modifier.width(16.dp))
    Icon(
      imageVector = Icons.Default.ArrowOutward,
      contentDescription = "Suggestion icon",
      modifier = Modifier.weight(weight = .1f)
    )
  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AppDarkPreview() {
  SearchSuggestionItem(
    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do" +
      " eiusmod tempor "
  ) {}
}

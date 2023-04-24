package com.github.whyrising.vancetube.modules.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.whyrising.vancetube.modules.designsystem.R
import com.github.whyrising.vancetube.modules.designsystem.data.ChannelVm
import com.github.whyrising.vancetube.modules.designsystem.data.PlaylistVm
import com.github.whyrising.vancetube.modules.designsystem.data.VideoViewModel

@Composable
fun ListItemPortrait(
  title: String,
  thumbnail: String,
  info: AnnotatedString,
  modifier: Modifier = Modifier,
  videoInfoTextStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
  thumbnailHeight: Dp,
  thumbnailContent: @Composable (BoxScope.() -> Unit),
  channelAvatar: @Composable (RowScope.() -> Unit)? = null
) {
  Column(modifier = Modifier.clickable { /*todo:*/ }) {
    Thumbnail(
      url = thumbnail,
      modifier = Modifier
        .fillMaxWidth()
        .height(thumbnailHeight),
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
        VideoItemInfo(info = info, textStyle = videoInfoTextStyle)
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
  thumbnailHeight: Dp,
  content: @Composable (BoxScope.() -> Unit),
  channelAvatar: @Composable (ColumnScope.() -> Unit)? = null
) {
  Row(
    modifier = modifier
      .testTag("video")
      .clickable { /*todo:*/ }
  ) {
    Thumbnail(
      url = thumbnail,
      modifier = Modifier
        .weight(.25f)
        .height(thumbnailHeight)
        .clip(RoundedCornerShape(8.dp)),
      content = content
    )

    Spacer(modifier = Modifier.width(16.dp))

    Column(modifier = Modifier.weight(.8f)) {
      VideoItemTitle(title = title)

      Spacer(modifier = Modifier.height(2.dp))

      VideoItemInfo(info = info)

      channelAvatar?.let { it() }
    }

    MoreButton()
  }
}

@Composable
fun VideoItemPortrait(
  modifier: Modifier = Modifier,
  videoInfoTextStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
  viewModel: VideoViewModel,
  thumbnailHeight: Dp
) {
  ListItemPortrait(
    title = viewModel.title,
    thumbnail = viewModel.thumbnail,
    info = viewModel.info,
    modifier = modifier.padding(top = 12.dp),
    videoInfoTextStyle = videoInfoTextStyle,
    thumbnailHeight = thumbnailHeight,
    thumbnailContent = {
      ThumbnailContent(viewModel)
    },
    channelAvatar = {
      ChannelAvatar(
        url = viewModel.uploaderAvatar,
        modifier = Modifier.size(40.dp)
      )
      Spacer(modifier = Modifier.width(16.dp))
    }
  )
}

@Composable
fun VideoItemLandscapeCompact(
  viewModel: VideoViewModel,
  thumbnailHeight: Dp
) {
  ListItemLandscape(
    title = viewModel.title,
    modifier = Modifier.padding(vertical = 8.dp),
    thumbnail = viewModel.thumbnail,
    info = viewModel.info,
    thumbnailHeight = thumbnailHeight,
    content = { ThumbnailContent(viewModel) }
  ) {
    Spacer(modifier = Modifier.height(12.dp))

    ChannelAvatar(
      url = viewModel.uploaderAvatar,
      modifier = Modifier.size(40.dp)
    )
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

@Composable
fun ChannelItem(
  modifier: Modifier = Modifier,
  avatarPaddingValues: PaddingValues = PaddingValues(0.dp),
  vm: ChannelVm
) {
  val typography = MaterialTheme.typography
  ListItem(
    modifier = modifier,
    leadingContent = {
      ChannelAvatar(
        url = vm.avatar,
        modifier = Modifier
          .padding(avatarPaddingValues)
          .padding(horizontal = 40.dp)
          .size(AVATAR_SIZE)
      )
    },
    headlineContent = {
      Text(text = vm.author, style = typography.labelLarge)
      val textStyle = typography.bodySmall.copy(
        color = LocalContentColor.current.copy(alpha = .6f),
        fontSize = 12.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = vm.handle, style = textStyle)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = "${vm.subCount} subscribers", style = textStyle)
      val colorScheme = MaterialTheme.colorScheme
      Button(
        modifier = Modifier
          .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(
          containerColor = colorScheme.onSurface,
          contentColor = colorScheme.surface
        ),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
      ) {
        Text(
          text = stringResource(R.string.subscribe),
          style = typography.labelMedium
        )
      }
    }
  )
}

@Composable
fun PlayListPortrait(
  modifier: Modifier = Modifier,
  viewModel: PlaylistVm,
  thumbnailHeight: Dp
) {
  ListItemPortrait(
    title = viewModel.title,
    thumbnail = viewModel.thumbnailUrl,
    info = AnnotatedString(viewModel.author),
    modifier = modifier.padding(top = 12.dp),
    thumbnailHeight = thumbnailHeight,
    thumbnailContent = { PlaylistThumbnailContent(viewModel) },
    channelAvatar = {}
  )
}

@Composable
fun PlayListLandscape(
  viewModel: PlaylistVm,
  thumbnailHeight: Dp
) {
  Row(
    modifier = Modifier
      .testTag("video")
      .padding(vertical = 8.dp)
      .clickable { /*todo:*/ }
  ) {
    ListItemLandscape(
      title = viewModel.title,
      thumbnail = viewModel.thumbnailUrl,
      info = AnnotatedString(viewModel.author),
      thumbnailHeight = thumbnailHeight,
      content = { PlaylistThumbnailContent(viewModel) }
    )
  }
}

/*
 * -- Previews -------------------------------------------------------------
 *
 */

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SearchSuggestionItemDarkPreview() {
  SearchSuggestionItem(
    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do" +
      " eiusmod tempor "
  ) {}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChannelItemDarkPreview() {
  ChannelItem(
    vm = ChannelVm(
      id = "authorId",
      author = "author",
      subCount = "14.1M",
      handle = "@authorId",
      avatar = ""
    )
  )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VideoItemPortraitPreview() {
  VideoItemPortrait(
    viewModel = VideoViewModel(
      id = "video.url",
      authorId = "authorId",
      title = "title title title title title title title title title title " +
        "title title ",
      thumbnail = "",
      length = "2:00",
      info = AnnotatedString("info")
    ),
    thumbnailHeight = rememberThumbnailHeightPortrait()
  )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VideoListItemLandscapeCompactPreview() {
  VideoItemLandscapeCompact(
    viewModel = VideoViewModel(
      id = "video.url",
      authorId = "authorId",
      title = "title title title title title title title title title title " +
        "title title ",
      thumbnail = "",
      length = "2:00",
      info = AnnotatedString("info")
    ),
    thumbnailHeight = rememberThumbnailHeightLandscape()
  )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlayListPortraitPreview() {
  PlayListPortrait(
    viewModel = PlaylistVm(
      author = "author",
      authorId = "id",
      title = "Title",
      videoCount = "13",
      thumbnailUrl = "",
      playlistId = "id",
      authorUrl = ""
    ),
    thumbnailHeight = rememberThumbnailHeightPortrait()
  )
}

@Preview(showBackground = true, uiMode = Configuration.ORIENTATION_LANDSCAPE)
@Composable
fun PlayListLandscapePreview() {
  PlayListLandscape(
    viewModel = PlaylistVm(
      author = "author",
      authorId = "id",
      title = "Title",
      videoCount = "13",
      thumbnailUrl = "",
      playlistId = "id",
      authorUrl = ""
    ),
    thumbnailHeight = rememberThumbnailHeightLandscape()
  )
}

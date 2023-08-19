package io.github.yahyatinani.tubeyou.core.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AuthorAvatar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ChannelAvatarLive
import com.github.yahyatinani.tubeyou.modules.designsystem.component.LARGE_AVATAR
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ListItemLandscape
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ListItemPortrait
import com.github.yahyatinani.tubeyou.modules.designsystem.component.SubscribeButton
import com.github.yahyatinani.tubeyou.modules.designsystem.component.rememberThumbnailHeightLandscape
import com.github.yahyatinani.tubeyou.modules.designsystem.component.rememberThumbnailHeightPortrait
import io.github.yahyatinani.tubeyou.core.viewmodels.ChannelVm
import io.github.yahyatinani.tubeyou.core.viewmodels.PlaylistVm
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoViewModel

@Composable
fun VideoItemPortrait(
  modifier: Modifier = Modifier,
  videoInfoTextStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp),
  viewModel: VideoViewModel,
  thumbnailHeight: Dp,
  play: () -> Unit = {}
) {
  ListItemPortrait(
    title = viewModel.title,
    thumbnail = viewModel.thumbnail,
    info = viewModel.info,
    modifier = modifier.padding(top = 12.dp),
    videoInfoTextStyle = videoInfoTextStyle,
    thumbnailHeight = thumbnailHeight,
    thumbnailContent = { ThumbnailContent(viewModel) },
    channelAvatar = {
      when {
        viewModel.isLiveStream -> ChannelAvatarLive(viewModel.uploaderAvatar)
        else -> AuthorAvatar(url = viewModel.uploaderAvatar)
      }
      Spacer(modifier = Modifier.width(16.dp))
    },
    onClick = play
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

    when {
      viewModel.isLiveStream -> ChannelAvatarLive(viewModel.uploaderAvatar)
      else -> AuthorAvatar(url = viewModel.uploaderAvatar)
    }
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
      AuthorAvatar(
        url = vm.avatar,
        modifier = Modifier
          .padding(avatarPaddingValues)
          .padding(horizontal = 40.dp),
        size = LARGE_AVATAR
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
      SubscribeButton()
    }
  )
}

@Composable
fun BoxScope.PlaylistThumbnailContent(viewModel: PlaylistVm) {
  val textStyle = MaterialTheme.typography.labelMedium
  Row(
    modifier = Modifier
      .align(alignment = Alignment.BottomCenter)
      .background(color = Color.Black.copy(alpha = .4f))
      .fillMaxWidth()
      .wrapContentHeight()
      .padding(vertical = 2.dp, horizontal = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Default.PlaylistPlay,
        contentDescription = "",
        modifier = Modifier.size(18.dp),
        tint = Color.White
      )
      Text(
        text = "Playlist",
        color = Color.White,
        style = textStyle
      )
    }
    Row {
      Text(
        text = viewModel.videoCount,
        color = Color.White,
        style = textStyle
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "videos",
        color = Color.White,
        style = textStyle
      )
    }
  }
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
    thumbnailContent = { PlaylistThumbnailContent(viewModel) }
  )
}

@Composable
fun PlayListLandscape(viewModel: PlaylistVm, thumbnailHeight: Dp) {
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
      title = "Title",
      videoCount = "13",
      thumbnailUrl = "",
      playlistId = "id",
      authorUrl = ""
    ),
    thumbnailHeight = rememberThumbnailHeightLandscape()
  )
}

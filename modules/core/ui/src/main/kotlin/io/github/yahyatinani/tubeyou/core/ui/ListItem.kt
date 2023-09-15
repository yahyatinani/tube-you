package io.github.yahyatinani.tubeyou.core.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.yahyatinani.tubeyou.modules.designsystem.component.AuthorAvatar
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ChannelAvatarLive
import com.github.yahyatinani.tubeyou.modules.designsystem.component.LARGE_AVATAR
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ListItemLandscape
import com.github.yahyatinani.tubeyou.modules.designsystem.component.ListItemPortrait
import com.github.yahyatinani.tubeyou.modules.designsystem.component.SubscribeButton
import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons
import io.github.yahyatinani.tubeyou.core.viewmodels.ChannelVm
import io.github.yahyatinani.tubeyou.core.viewmodels.PlaylistVm
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm

@Composable
fun VideoItemPortrait(
  modifier: Modifier = Modifier,
  videoInfoTextStyle: TextStyle,
  viewModel: VideoVm,
  onClick: () -> Unit
) {
  ListItemPortrait(
    title = viewModel.title,
    thumbnail = viewModel.thumbnail,
    info = viewModel.info,
    modifier = modifier
      .padding(top = 12.dp),
    videoInfoTextStyle = videoInfoTextStyle,
    thumbnailContent = { ThumbnailContent(viewModel) },
    channelAvatar = {
      when {
        viewModel.isLiveStream -> ChannelAvatarLive(viewModel.uploaderAvatar)
        else -> AuthorAvatar(url = viewModel.uploaderAvatar)
      }
      Spacer(modifier = Modifier.width(16.dp))
    },
    onClick = onClick
  )
}

@Composable
fun VideoItemLandscapeCompact(
  viewModel: VideoVm,
  onClick: () -> Unit
) {
  ListItemLandscape(
    title = viewModel.title,
    modifier = Modifier.padding(vertical = 8.dp),
    thumbnail = viewModel.thumbnail,
    info = viewModel.info,
    channelAvatar = {
      Spacer(modifier = Modifier.height(12.dp))

      when {
        viewModel.isLiveStream -> ChannelAvatarLive(viewModel.uploaderAvatar)
        else -> AuthorAvatar(url = viewModel.uploaderAvatar)
      }
    },
    onClick = onClick
  ) {
    ThumbnailContent(viewModel)
  }
}

@Composable
fun VideoItemCompact(
  viewModel: VideoVm,
  videoInfoTextStyle: TextStyle,
  isPortraitMode: Boolean,
  onClickVideo: (VideoVm) -> Unit
) = when {
  isPortraitMode -> VideoItemPortrait(
    videoInfoTextStyle = videoInfoTextStyle,
    viewModel = viewModel,
    onClick = { onClickVideo(viewModel) }
  )

  else -> VideoItemLandscapeCompact(
    viewModel = viewModel,
    onClick = { onClickVideo(viewModel) }
  )
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
      .background(color = Color.DarkGray.copy(alpha = .8f))
      .fillMaxWidth()
      .wrapContentHeight()
      .padding(vertical = 2.dp, horizontal = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = TyIcons.PlayListPlay,
        contentDescription = "",
        modifier = Modifier.width(24.dp),
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
  videoInfoTextStyle: TextStyle
) {
  ListItemPortrait(
    title = viewModel.title,
    modifier = modifier.padding(top = 12.dp),
    thumbnail = viewModel.thumbnailUrl,
    info = AnnotatedString(viewModel.author),
    videoInfoTextStyle = videoInfoTextStyle,
    thumbnailContent = { PlaylistThumbnailContent(viewModel) }
  )
}

@Composable
fun PlayListLandscape(
  viewModel: PlaylistVm,
  onClick: () -> Unit
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
      onClick = onClick
    ) {
      PlaylistThumbnailContent(viewModel)
    }
  }
}

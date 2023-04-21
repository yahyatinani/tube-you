package com.github.whyrising.vancetube.modules.panel.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.whyrising.recompose.watch
import com.github.whyrising.vancetube.modules.core.keywords.common.search_results
import com.github.whyrising.vancetube.modules.designsystem.component.ChannelItem
import com.github.whyrising.vancetube.modules.designsystem.component.PlayListPortrait
import com.github.whyrising.vancetube.modules.designsystem.component.VideoItemPortrait
import com.github.whyrising.vancetube.modules.designsystem.component.VideoListItemLandscapeCompact
import com.github.whyrising.vancetube.modules.designsystem.core.formatSeconds
import com.github.whyrising.vancetube.modules.designsystem.core.formatSubCount
import com.github.whyrising.vancetube.modules.designsystem.core.formatVideoInfo
import com.github.whyrising.vancetube.modules.designsystem.core.formatViews
import com.github.whyrising.vancetube.modules.designsystem.data.ChannelVm
import com.github.whyrising.vancetube.modules.designsystem.data.PlaylistVm
import com.github.whyrising.vancetube.modules.designsystem.data.SearchVm
import com.github.whyrising.vancetube.modules.designsystem.data.VideoViewModel
import com.github.whyrising.vancetube.modules.panel.common.R.string.views_label
import com.github.whyrising.y.core.v

fun formatVideo(
  video: Video,
  viewsLabel: Any
) = VideoViewModel(
  id = video.videoId,
  authorId = video.authorId!!,
  title = video.title,
  thumbnail = video.videoThumbnails[1].url,
  length = formatSeconds(video.lengthSeconds.toLong()),
  info = formatVideoInfo(
    author = video.author!!,
    authorId = video.authorId,
    viewCount = formatViews(video.viewCount!!),
    viewsLabel = viewsLabel as String,
    publishedText = video.publishedText!!
  )
)

fun formatVideos(
  videoDataList: List<Video>,
  viewsLabel: Any
): List<VideoViewModel> = videoDataList.fold(v()) { acc, video ->
  acc.conj(formatVideo(video, viewsLabel))
}

fun formatChannel(channel: Channel) = ChannelVm(
  id = channel.authorId,
  author = channel.author,
  subCount = formatSubCount(channel.subCount.toLong()),
  handle = "@${channel.author.replace(" ", "")}",
  authorThumbnail = "https:${channel.authorThumbnails[1].url}"
)

fun formatPlayList(r: Playlist) = PlaylistVm(
  title = r.title,
  author = r.author,
  authorId = r.authorId,
  authorUrl = r.authorUrl,
  playlistId = r.playlistId,
  thumbnailUrl = r.videos[0].videoThumbnails[0].url,
  videoCount = "${r.videoCount}"
)

const val SEARCH_ROUTE = "search_results"

fun NavGraphBuilder.searchResults(route: String, orientation: Int) {
  composable(route = "$route/$SEARCH_ROUTE") {
    val listState = rememberLazyListState()
    val videos = watch<SearchVm>(v(search_results, stringResource(views_label)))
    LazyColumn(
      state = listState,
      modifier = Modifier
        .testTag("search_list")
        .fillMaxSize()
    ) {
      itemsIndexed(videos.value, key = { index, _ -> index }) { index, vm ->
        if (vm is VideoViewModel) {
          if (index > 1 && videos.value[index - 1] !is VideoViewModel) {
            Divider(thickness = 6.dp, color = DarkGray)
          }
          when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
              VideoItemPortrait(
                modifier = Modifier.padding(start = 12.dp),
                viewModel = vm
              )
            }

            else -> VideoListItemLandscapeCompact(vm)
          }
        } else {
          if (index != 0) Divider(thickness = 6.dp, color = DarkGray)
          if (vm is ChannelVm) {
            ChannelItem(modifier = Modifier.fillMaxWidth(), vm = vm)
          } else if (vm is PlaylistVm) {
            PlayListPortrait(
              modifier = Modifier.padding(start = 12.dp),
              viewModel = vm
            )
          }
        }
      }
    }
  }
}

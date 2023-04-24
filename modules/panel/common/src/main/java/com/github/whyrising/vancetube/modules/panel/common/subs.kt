package com.github.whyrising.vancetube.modules.panel.common

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.whyrising.recompose.watch
import com.github.whyrising.vancetube.modules.core.keywords.common.search_results
import com.github.whyrising.vancetube.modules.designsystem.component.ChannelItem
import com.github.whyrising.vancetube.modules.designsystem.component.PlayListLandscape
import com.github.whyrising.vancetube.modules.designsystem.component.PlayListPortrait
import com.github.whyrising.vancetube.modules.designsystem.component.VideoItemLandscapeCompact
import com.github.whyrising.vancetube.modules.designsystem.component.VideoItemPortrait
import com.github.whyrising.vancetube.modules.designsystem.core.convertTimestamp
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

/**
 * Checkout https://github.com/TeamNewPipe/NewPipeExtractor/pull/268
 * mqdefault.jpg 39.76kb
 * maxresdefault.jpg 306.98 kb
 */
private fun highQuality(thumbnail: String) =
  thumbnail.replace("hqdefault.jpg", "mqdefault.jpg")

fun formatVideo(
  video: Video,
  viewsLabel: Any
): VideoViewModel {
  val isLiveStream = video.duration == -1L
  val authorId = video.uploaderUrl!!
  val isUpcoming = video.views == -1L
  val info = if (isUpcoming) {
//    formatUpcomingInfo(video.uploaderName!!, video.uploaded)
    formatVideoInfo(
      author = video.uploaderName!!,
      authorId = authorId,
      viewCount = "Scheduled for",
      viewsLabel = convertTimestamp(video.uploaded)
    )
  } else {
    val viewCount = formatViews(video.views!!)
    if (isLiveStream) {
      formatVideoInfo(
        author = video.uploaderName!!,
        authorId = authorId,
        viewCount = viewCount,
        viewsLabel = "watching"
      )
    } else {
      formatVideoInfo(
        author = video.uploaderName!!,
        authorId = authorId,
        viewCount = viewCount,
        viewsLabel = viewsLabel as String,
        publishedText = video.uploadedDate!!
      )
    }
  }
  return VideoViewModel(
    id = video.url,
    authorId = authorId,
    title = video.title,
    thumbnail = highQuality(video.thumbnail),
    length = formatSeconds(video.duration),
    info = info,
    uploaderAvatar = video.uploaderAvatar,
    isUpcoming = isUpcoming,
    isLiveStream = isLiveStream,
    isShort = video.isShort
  )
}

fun formatVideos(
  videoDataList: List<Video>,
  viewsLabel: Any
): List<VideoViewModel> = videoDataList.fold(v()) { acc, video ->
  acc.conj(formatVideo(video, viewsLabel))
}

fun formatChannel(channel: Channel) = ChannelVm(
  id = channel.url,
  author = channel.name,
  subCount = formatSubCount(channel.subscribers.toLong()),
  handle = "@${channel.name.replace(" ", "")}",
  avatar = channel.thumbnail
)

fun formatPlayList(r: Playlist) = PlaylistVm(
  title = r.name,
  author = r.uploaderName,
  authorId = r.uploaderUrl,
  authorUrl = r.uploaderUrl,
  playlistId = r.url,
  thumbnailUrl = highQuality(r.thumbnail),
  videoCount = "${r.videos}"
)

const val SEARCH_ROUTE = "search_results"

fun NavGraphBuilder.searchResults(
  route: String,
  orientation: Int,
  thumbnailHeight: Dp
) {
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
        val isPortrait = orientation == ORIENTATION_PORTRAIT
        if (vm is VideoViewModel) {
          if (
            index > 1 &&
            videos.value[index - 1] !is VideoViewModel &&
            isPortrait
          ) {
            Divider(thickness = 6.dp, color = DarkGray)
          }
          when {
            isPortrait -> VideoItemPortrait(
              viewModel = vm,
              thumbnailHeight = thumbnailHeight
            )

            else -> VideoItemLandscapeCompact(
              viewModel = vm,
              thumbnailHeight = thumbnailHeight
            )
          }
        } else {
          if (index != 0 && isPortrait) {
            Divider(thickness = 6.dp, color = DarkGray)
          }

          when (vm) {
            is ChannelVm -> {
              ChannelItem(
                vm = vm,
                modifier = Modifier
                  .clickable { /*TODO*/ }
                  .fillMaxWidth(),
                avatarPaddingValues = when {
                  isPortrait -> PaddingValues()
                  else -> PaddingValues(horizontal = 24.dp)
                }
              )
            }

            is PlaylistVm -> {
              when {
                isPortrait -> PlayListPortrait(
                  modifier = Modifier.padding(start = 12.dp),
                  viewModel = vm,
                  thumbnailHeight = thumbnailHeight
                )

                else -> {
                  PlayListLandscape(
                    viewModel = vm,
                    thumbnailHeight = thumbnailHeight
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

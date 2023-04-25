package com.github.whyrising.vancetube.modules.panel.common

import android.content.res.Resources
import com.github.whyrising.vancetube.modules.designsystem.core.convertTimestamp
import com.github.whyrising.vancetube.modules.designsystem.core.formatSeconds
import com.github.whyrising.vancetube.modules.designsystem.core.formatSubCount
import com.github.whyrising.vancetube.modules.designsystem.core.formatVideoInfo
import com.github.whyrising.vancetube.modules.designsystem.core.formatViews
import com.github.whyrising.vancetube.modules.designsystem.data.ChannelVm
import com.github.whyrising.vancetube.modules.designsystem.data.PlaylistVm
import com.github.whyrising.vancetube.modules.designsystem.data.VideoViewModel
import com.github.whyrising.y.core.v
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <T> T.letIf(b: Boolean, block: (T) -> T): T {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return if (b) block(this) else this
}

/**
 * Checkout https://github.com/TeamNewPipe/NewPipeExtractor/pull/268
 * mqdefault.jpg 39.76kb
 * maxresdefault.jpg 306.98 kb
 */
private fun highQuality(thumbnail: String) =
  thumbnail.replace("hqdefault.jpg", "mqdefault.jpg")

fun formatVideo(video: Video, resources: Resources): VideoViewModel {
  val isLiveStream = video.duration == -1L
  val authorId = video.uploaderUrl!!
  val isUpcoming = video.views == -1L
  val info = if (isUpcoming) {
    formatVideoInfo(
      author = video.uploaderName!!,
      authorId = authorId,
      text1 = resources.getString(R.string.scheduled_for),
      text2 = convertTimestamp(video.uploaded)
    )
  } else {
    val viewCount = formatViews(video.views!!)
    when {
      isLiveStream -> formatVideoInfo(
        author = video.uploaderName!!,
        authorId = authorId,
        text1 = viewCount,
        text2 = resources.getString(R.string.watching)
      )

      else -> formatVideoInfo(
        author = video.uploaderName!!,
        authorId = authorId,
        text1 = viewCount,
        text2 = resources.getString(R.string.views_label),
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
  resources: Resources
): List<VideoViewModel> = videoDataList.fold(v()) { acc, video ->
  acc.conj(formatVideo(video, resources))
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

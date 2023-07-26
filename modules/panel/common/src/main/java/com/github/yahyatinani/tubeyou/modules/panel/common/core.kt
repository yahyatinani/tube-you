package com.github.yahyatinani.tubeyou.modules.panel.common

import android.content.res.Resources
import com.github.yahyatinani.tubeyou.modules.designsystem.core.convertTimestamp
import com.github.yahyatinani.tubeyou.modules.designsystem.core.formatSeconds
import com.github.yahyatinani.tubeyou.modules.designsystem.core.formatSubCount
import com.github.yahyatinani.tubeyou.modules.designsystem.core.formatVideoInfo
import com.github.yahyatinani.tubeyou.modules.designsystem.core.formatViews
import com.github.yahyatinani.tubeyou.modules.designsystem.data.ChannelVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PlaylistVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideoViewModel
import com.github.yahyatinani.tubeyou.modules.panel.common.search.Channel
import com.github.yahyatinani.tubeyou.modules.panel.common.search.Playlist
import com.github.yahyatinani.tubeyou.modules.panel.common.search.Video
import io.github.yahyatinani.y.core.v
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
 *
 * default.jpg 8.73 kb
 * mqdefault.jpg 39.76kb
 * hqdefault.jpg 76.84 kb
 * sddefault.jpg 121.76 kb
 * hq720.jpg 306.98 kb
 * maxresdefault.jpg 306.98 kb
 *
 */
private fun highQuality(thumbnail: String) =
  thumbnail.replace("hqdefault.jpg", "sddefault.jpg")

fun formatVideo(
  video: Video,
  resources: Resources
): VideoViewModel {
  val isUpcoming = video.views == -1L && video.duration == -1L
  val authorId = video.uploaderUrl!!
  val isLiveStream = video.duration == -1L
  val uploaded = video.uploadedDate
  val uploaderName = video.uploaderName!!

  val vm = VideoViewModel(
    id = video.url,
    authorId = authorId,
    uploaderName = uploaderName,
    title = video.title,
    thumbnail = highQuality(video.thumbnail),
    length = formatSeconds(video.duration),
    uploaded = uploaded ?: "",
    uploaderAvatar = video.uploaderAvatar,
    isUpcoming = isUpcoming,
    isLiveStream = isLiveStream,
    isShort = video.isShort
  )

  return if (isUpcoming) {
    vm.copy(
      info = formatVideoInfo(
        author = uploaderName,
        authorId = authorId,
        text1 = resources.getString(R.string.scheduled_for),
        text2 = convertTimestamp(video.uploaded)
      )
    )
  } else {
    val viewCount = formatViews(video.views!!)
    when {
      isLiveStream -> {
        val label = resources.getString(R.string.watching)
        vm.copy(
          info = formatVideoInfo(
            author = uploaderName,
            authorId = authorId,
            text1 = viewCount,
            text2 = label
          ),
          viewCount = "$viewCount $label"
        )
      }

      else -> {
        val label = resources.getString(R.string.views_label)
        vm.copy(
          info = formatVideoInfo(
            author = uploaderName,
            authorId = authorId,
            text1 = viewCount,
            text2 = label,
            publishedText = video.uploadedDate
          ),
          viewCount = "$viewCount $label"
        )
      }
    }
  }
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
  authorUrl = r.uploaderUrl,
  playlistId = r.url,
  thumbnailUrl = highQuality(r.thumbnail),
  videoCount = "${r.videos}"
)

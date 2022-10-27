package com.github.whyrising.vancetube.modules.panel.common

import com.github.whyrising.vancetube.modules.designsystem.core.formatSeconds
import com.github.whyrising.vancetube.modules.designsystem.core.formatVideoInfo
import com.github.whyrising.vancetube.modules.designsystem.core.formatViews
import com.github.whyrising.vancetube.modules.designsystem.data.VideoViewModel
import com.github.whyrising.y.core.v

fun formatVideos(
  videoDataList: List<VideoData>,
  viewsLabel: Any
): List<VideoViewModel> = videoDataList.fold(v()) { acc, video ->
  acc.conj(
    VideoViewModel(
      id = video.videoId,
      authorId = video.authorId,
      title = video.title,
      thumbnail = video.videoThumbnails[4].url,
      length = formatSeconds(video.lengthSeconds),
      info = formatVideoInfo(
        author = video.author,
        authorId = video.authorId,
        viewCount = formatViews(video.viewCount),
        viewsLabel = viewsLabel as String,
        publishedText = video.publishedText
      )
    )
  )
}

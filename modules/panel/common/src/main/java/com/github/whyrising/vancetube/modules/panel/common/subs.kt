package com.github.whyrising.vancetube.modules.panel.common

import com.github.whyrising.vancetube.modules.designsystem.core.formatSeconds
import com.github.whyrising.vancetube.modules.designsystem.core.formatVideoInfo
import com.github.whyrising.vancetube.modules.designsystem.core.formatViews
import com.github.whyrising.vancetube.modules.designsystem.data.VideoViewModel
import com.github.whyrising.y.core.v

fun formatVideos(
  videoDataList: List<VideoData>,
  viewsLabel: Any
): List<VideoViewModel> = videoDataList.fold(v()) { acc, videoMetadata ->
  acc.conj(
    VideoViewModel(
      id = videoMetadata.videoId,
      authorId = videoMetadata.authorId,
      title = videoMetadata.title,
      thumbnail = videoMetadata.videoThumbnails[4].url,
      length = formatSeconds(videoMetadata.lengthSeconds),
      info = formatVideoInfo(
        author = videoMetadata.author,
        authorId = videoMetadata.authorId,
        viewCount = formatViews(videoMetadata.viewCount),
        viewsLabel = viewsLabel as String,
        publishedText = videoMetadata.publishedText
      )
    )
  )
}

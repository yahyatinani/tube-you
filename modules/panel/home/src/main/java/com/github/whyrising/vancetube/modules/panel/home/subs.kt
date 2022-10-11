package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.home.popular_vids
import com.github.whyrising.vancetube.modules.designsystem.core.formatSeconds
import com.github.whyrising.vancetube.modules.designsystem.core.formatVideoInfo
import com.github.whyrising.vancetube.modules.designsystem.core.formatViews
import com.github.whyrising.vancetube.modules.designsystem.data.VideoViewModel
import com.github.whyrising.vancetube.modules.designsystem.data.Videos
import com.github.whyrising.vancetube.modules.designsystem.data.VideosPanelState
import com.github.whyrising.vancetube.modules.panel.common.AppDb
import com.github.whyrising.vancetube.modules.panel.common.States
import com.github.whyrising.vancetube.modules.panel.common.VideoData
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getFrom
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

// -- Subs ---------------------------------------------------------------------

/**
 * Call this lazy global property to initialise all [Home] page subscriptions.
 * @return [Unit]
 */
val regHomeSubs by lazy {
  regSub<AppDb, Any?>(home.state) { db, _ ->
    db[home.panel]
  }

  regSub<AppDb, VideosPanelState>(
    queryId = home.view_model,
    signalsFn = { subscribe(v(home.state)) },
    computationFn = { homeDb, previousVal, (_, viewsLabel) ->
      when (homeDb[home.state] as States) {
        States.Loading -> VideosPanelState(isLoading = true)
        States.Refreshing -> VideosPanelState(
          isRefreshing = true,
          showList = true,
          videos = previousVal!!.videos
        )

        States.Loaded -> {
          val videos: List<VideoData> = getFrom(homeDb, popular_vids)!!
          VideosPanelState(
            showList = true,
            videos = Videos(formatVideos(videos, viewsLabel))
          )
        }

        States.Failed -> TODO()
      }
    }
  )
}

package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.home.popular_vids
import com.github.whyrising.vancetube.modules.designsystem.data.Videos
import com.github.whyrising.vancetube.modules.designsystem.data.VideosPanelVm
import com.github.whyrising.vancetube.modules.panel.common.AppDb
import com.github.whyrising.vancetube.modules.panel.common.States
import com.github.whyrising.vancetube.modules.panel.common.VideoData
import com.github.whyrising.vancetube.modules.panel.common.formatVideos
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.v

// -- Subs ---------------------------------------------------------------------

/**
 * Call this lazy global property to initialise all [Home] page subscriptions.
 * @return [Unit]
 */
val regHomeSubs by lazy {
  regSub<AppDb, Any?>(home.state) { db, _ ->
    db[home.panel]
  }

  regSub<AppDb?, VideosPanelVm>(
    queryId = home.view_model,
    signalsFn = { subscribe(v(home.state)) },
    initialValue = VideosPanelVm(),
    computationFn = { homeDb, currentValue, (_, viewsLabel) ->
      when (get<States>(homeDb?.get(home.state), 0)) {
        null, States.Loading -> VideosPanelVm(isLoading = true)

        States.Refreshing -> VideosPanelVm(
          isRefreshing = true,
          showList = true,
          videos = currentValue.videos
        )

        States.Loaded -> {
          val videos: List<VideoData> = get(homeDb, popular_vids)!!
          VideosPanelVm(
            showList = true,
            videos = Videos(formatVideos(videos, viewsLabel))
          )
        }

        States.Failed -> VideosPanelVm(error = get(homeDb, home.error)!!)
      }
    }
  )
}

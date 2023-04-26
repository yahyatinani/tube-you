package com.github.yahyatinani.tubeyou.modules.panel.home

import android.content.res.Resources
import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.popular_vids
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos
import com.github.yahyatinani.tubeyou.modules.designsystem.data.VideosPanelVm
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.States
import com.github.yahyatinani.tubeyou.modules.panel.common.formatVideos

fun getRegHomeSubs() {
  regSub<AppDb>(home.db) { db, _ ->
    db[HOME_GRAPH_ROUTE]
  }

  regSub<AppDb?, VideosPanelVm>(
    queryId = home.view_model,
    signalsFn = { subscribe(v(home.db)) },
    initialValue = VideosPanelVm(isLoading = true)
  ) { homeDb, currentValue, (_, resources) ->
    when (get<States>(homeDb?.get(home.state), 0)) {
      null, States.Loading -> VideosPanelVm(isLoading = true)

      States.Refreshing -> VideosPanelVm(
        isRefreshing = true,
        showList = true,
        videos = currentValue.videos
      )

      States.Loaded -> {
        VideosPanelVm(
          showList = true,
          videos = Videos(
            formatVideos(
              videoDataList = get(homeDb, popular_vids) ?: v(),
              resources = resources as Resources
            )
          )
        )
      }

      States.Failed -> VideosPanelVm(error = get(homeDb, home.error)!!)
    }
  }
}

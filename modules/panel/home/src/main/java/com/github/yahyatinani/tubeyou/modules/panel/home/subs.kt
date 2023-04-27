package com.github.yahyatinani.tubeyou.modules.panel.home

import android.content.res.Resources
import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.popular_vids
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PanelVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.States
import com.github.yahyatinani.tubeyou.modules.panel.common.formatVideos

fun getRegHomeSubs() {
  regSub<AppDb>(home.db) { db, _ ->
    db[HOME_GRAPH_ROUTE]
  }

  regSub<AppDb?, PanelVm>(
    queryId = home.view_model,
    signalsFn = { subscribe(v(home.db)) },
    initialValue = PanelVm.Loading
  ) { homeDb, currentValue, (_, resources) ->
    when (get<States>(homeDb, home.state)) {
      null, States.Loading -> PanelVm.Loading

      States.Refreshing -> PanelVm.Refreshing(currentValue.videos)

      States.Loaded -> PanelVm.Loaded(
        videos = Videos(
          formatVideos(
            videoDataList = get(homeDb, popular_vids) ?: v(),
            resources = resources as Resources
          )
        )
      )

      States.Failed -> PanelVm.Error(error = get(homeDb, home.error)!!)
    }
  }
}

package com.github.yahyatinani.tubeyou.modules.panel.home

import android.content.res.Resources
import com.github.whyrising.recompose.regSub
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PanelVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.PanelStates
import com.github.yahyatinani.tubeyou.modules.panel.common.PanelStates.FAILED
import com.github.yahyatinani.tubeyou.modules.panel.common.PanelStates.LOADED
import com.github.yahyatinani.tubeyou.modules.panel.common.PanelStates.LOADING
import com.github.yahyatinani.tubeyou.modules.panel.common.PanelStates.REFRESHING
import com.github.yahyatinani.tubeyou.modules.panel.common.formatVideos
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm

fun homeFsmState(db: AppDb): Any? =
  getIn(db, l(HOME_GRAPH_ROUTE, home.fsm_state))

fun <R> homeContent(stateMap: Any?): R = get(stateMap, home.content)!!

fun regHomeSubs() {
  regSub(home.fsm_state, ::homeFsmState)

  regSub<Any?, PanelVm>(
    queryId = home.view_model,
    initialValue = PanelVm.Loading,
    v(home.fsm_state)
  ) { stateMap, currentValue, (_, resources) ->
    when (get<PanelStates>(stateMap, fsm._state)) {
      null, LOADING -> PanelVm.Loading
      REFRESHING -> PanelVm.Refreshing(currentValue.videos)
      FAILED -> PanelVm.Error(error = homeContent(stateMap))
      LOADED -> PanelVm.Loaded(
        videos = Videos(
          formatVideos(homeContent(stateMap), resources as Resources)
        )
      )
    }
  }
}

package com.github.yahyatinani.tubeyou.modules.panel.home

import android.content.res.Resources
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
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.getIn
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.v

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

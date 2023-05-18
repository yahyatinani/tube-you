package com.github.yahyatinani.tubeyou.modules.panel.home

import android.content.res.Resources
import com.github.whyrising.recompose.regSub
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PanelVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.States
import com.github.yahyatinani.tubeyou.modules.panel.common.formatVideos
import com.github.yahyatinani.tubeyou.modules.panel.common.search.Video

fun homeFsmState(db: AppDb): Any? =
  getIn(db, l(HOME_GRAPH_ROUTE, home.state))

fun getRegHomeSubs() {
  regSub(home.fsm_state, ::homeFsmState)

  regSub<PersistentVector<Any?>?, PanelVm>(
    queryId = home.view_model,
    initialValue = PanelVm.Loading,
    v(home.fsm_state)
  ) { homeFsmState, currentValue, (_, resources) ->
    if (homeFsmState == null) return@regSub PanelVm.Loading

    when (homeFsmState[0] as States?) {
      null, States.Loading -> PanelVm.Loading

      States.Refreshing -> PanelVm.Refreshing(currentValue.videos)

      States.Loaded -> {
        PanelVm.Loaded(
          videos = Videos(
            formatVideos(
              videoDataList = homeFsmState[1] as List<Video>,
              resources = resources as Resources
            )
          )
        )
      }

      States.Failed -> PanelVm.Error(error = homeFsmState[1] as Int)
    }
  }
}

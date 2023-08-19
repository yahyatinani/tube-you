package io.github.yahyatinani.tubeyou.modules.feature.home.subs

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.yahyatinani.tubeyou.modules.core.keywords.States
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.FAILED
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.LOADED
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.LOADING
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.REFRESHING
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.tubeyou.common.AppDb
import io.github.yahyatinani.tubeyou.core.viewmodels.PanelVm
import io.github.yahyatinani.tubeyou.core.viewmodels.Videos
import io.github.yahyatinani.tubeyou.core.viewmodels.formatVideos
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.HOME_GRAPH_ROUTE
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.getIn
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.v

fun homeFsmState(db: AppDb): Any? =
  getIn(db, l(HOME_GRAPH_ROUTE, home.fsm_state))

fun <R> homeContent(stateMap: Any?): R = get(stateMap, home.content)!!

@Composable
fun RegHomeSubs() {
  remember {
    regSub(home.fsm_state, ::homeFsmState)

    regSub<Any?, PanelVm>(
      queryId = home.view_model,
      initialValue = PanelVm.Loading,
      v(home.fsm_state)
    ) { stateMap, currentValue, (_, resources) ->
      when (get<States>(stateMap, fsm._state)) {
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
    true
  }
}

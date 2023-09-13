package io.github.yahyatinani.tubeyou.modules.feature.home.subs

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.FAILED
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.LOADED
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.LOADING
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.REFRESHING
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.tubeyou.common.AppDb
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.VideoVm
import io.github.yahyatinani.tubeyou.core.viewmodels.formatVideos
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.HOME_GRAPH_ROUTE
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.getIn
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun homeFsmState(db: AppDb): Any? =
  getIn(db, l(HOME_GRAPH_ROUTE, home.fsm_state))

fun <R> homeContent(stateMap: Any?): R = get(stateMap, home.content)!!

@Composable
fun RegHomeSubs() {
  remember {
    regSub(home.fsm_state, ::homeFsmState)

    val initialHomeUiState = m(
      common.state to LOADING,
      home.content to UIState(l<VideoVm>())
    )

    regSub<Any?, UIState>(
      queryId = home.view_model,
      initialValue = UIState(initialHomeUiState),
      v(home.fsm_state)
    ) { stateMap, currentValue, (_, resources) ->
      val nextState = get(stateMap, fsm._state) ?: LOADING
      val data = initialHomeUiState.assoc(common.state, nextState)
      UIState(
        data = when (nextState) {
          LOADING -> data
          REFRESHING -> {
            data.assoc(home.content, homeContent(currentValue.data))
          }

          LOADED -> data.assoc(
            home.content,
            UIState(formatVideos(homeContent(stateMap), resources as Resources))
          )

          FAILED -> data.assoc(common.error, homeContent(stateMap))

          else -> TODO()
        }
      )
    }
    true
  }
}

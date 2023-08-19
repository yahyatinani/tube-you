package io.github.yahyatinani.tubeyou.modules.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.tubeyou.modules.feature.home.cofx.RetRegHomeCofx
import io.github.yahyatinani.tubeyou.modules.feature.home.events.RegHomeEvents
import io.github.yahyatinani.tubeyou.modules.feature.home.screen.HomeRoute
import io.github.yahyatinani.tubeyou.modules.feature.home.subs.RegHomeSubs
import io.github.yahyatinani.y.core.v

const val HOME_GRAPH_ROUTE = "home_graph"
const val HOME_ROUTE = "home_route"

@Composable
internal fun InitHome() {
  RegHomeSubs()
  RetRegHomeCofx()
  RegHomeEvents()
  LaunchedEffect(Unit) {
    dispatch(v(home.fsm, home.load))
  }
}

fun NavGraphBuilder.homeGraph(
  isCompact: Boolean,
  orientation: Int
) {
  navigation(
    route = HOME_GRAPH_ROUTE,
    startDestination = HOME_ROUTE
  ) {
    composable(route = HOME_ROUTE) {
      InitHome()
      HomeRoute(
        orientation = orientation,
        isCompact = isCompact,
        onPullRefresh = { dispatch(v(home.fsm, home.refresh)) },
        onClickVideo = {
          dispatch(v("stream_panel_fsm", common.play_video, it))
        }
      )
    }
  }
}

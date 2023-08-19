package io.github.yahyatinani.tubeyou.modules.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.yahyatinani.tubeyou.modules.designsystem.component.thumbnailHeight
import io.github.yahyatinani.tubeyou.modules.feature.home.screen.HomeRoute

const val HOME_GRAPH_ROUTE = "home_graph"
const val HOME_ROUTE = "home_route"

fun NavGraphBuilder.homeGraph() {
  navigation(
    route = HOME_GRAPH_ROUTE,
    startDestination = HOME_ROUTE
  ) {
    composable(route = HOME_ROUTE) {
      HomeRoute(orientation = 1, thumbnailHeight = thumbnailHeight())
    }
  }
}

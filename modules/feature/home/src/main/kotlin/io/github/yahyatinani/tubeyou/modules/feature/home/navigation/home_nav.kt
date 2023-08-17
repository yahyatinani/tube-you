package io.github.yahyatinani.tubeyou.modules.feature.home.navigation

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val HOME_GRAPH_ROUTE = "home_graph"
const val HOME_ROUTE = "home_route"

fun NavGraphBuilder.homeGraph() {
  navigation(
    route = HOME_GRAPH_ROUTE,
    startDestination = HOME_ROUTE
  ) {
    composable(route = HOME_ROUTE) {
      Text(text = "Home")
    }
  }
}

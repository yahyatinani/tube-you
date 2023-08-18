package io.github.yahyatinani.tubeyou.modules.feature.library

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val YOU_GRAPH_ROUTE = "you_graph"
const val YOU_ROUTE = "you_route"

fun NavGraphBuilder.youGraph() {
  navigation(
    route = YOU_GRAPH_ROUTE,
    startDestination = YOU_ROUTE
  ) {
    composable(route = YOU_ROUTE) {
      Text(text = "todo: you not implement, yet.")
    }
  }
}

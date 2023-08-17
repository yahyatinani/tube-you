package io.github.yahyatinani.tubeyou.modules.feature.subscriptions

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val SUBSCRIPTIONS_GRAPH_ROUTE = "subscriptions_graph"
const val SUBSCRIPTIONS_ROUTE = "subscriptions_route"

fun NavGraphBuilder.subscriptionsGraph() {
  navigation(
    route = SUBSCRIPTIONS_GRAPH_ROUTE,
    startDestination = SUBSCRIPTIONS_ROUTE
  ) {
    composable(route = SUBSCRIPTIONS_ROUTE) {
      Text(text = "todo: subscriptions not implement, yet.")
    }
  }
}

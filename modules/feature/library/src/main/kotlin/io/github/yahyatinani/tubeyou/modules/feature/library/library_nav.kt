package io.github.yahyatinani.tubeyou.modules.feature.library

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val LIBRARY_GRAPH_ROUTE = "library_graph"
const val LIBRARY_ROUTE = "library_route"

fun NavGraphBuilder.libraryGraph() {
  navigation(
    route = LIBRARY_GRAPH_ROUTE,
    startDestination = LIBRARY_ROUTE
  ) {
    composable(route = LIBRARY_ROUTE) {
      Text(text = "todo: library not implement, yet.")
    }
  }
}

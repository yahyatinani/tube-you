package io.github.yahyatinani.tubeyou.ui.modules.feature.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.screen.SearchRoute

const val SEARCH_ROUTE = "search_route"

fun NavGraphBuilder.searchScreen(
  rootGraphRoute: String,
  orientation: Int
) {
  composable(route = "$rootGraphRoute/$SEARCH_ROUTE") {
    SearchRoute(orientation = orientation)
  }
}

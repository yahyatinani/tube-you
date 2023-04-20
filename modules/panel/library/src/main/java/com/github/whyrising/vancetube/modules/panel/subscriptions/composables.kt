package com.github.whyrising.vancetube.modules.panel.subscriptions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.whyrising.vancetube.modules.core.keywords.SUBSCRIPTIONS_GRAPH_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.SUBSCRIPTIONS_ROUTE
import com.github.whyrising.vancetube.modules.panel.common.searchResults

fun NavGraphBuilder.subscriptions(orientation: Int) {
  composable(route = SUBSCRIPTIONS_ROUTE) {
    Surface(modifier = Modifier.fillMaxSize()) {
      Text(text = "TODO: subscriptions")
    }
  }
}

fun NavGraphBuilder.subsGraph(isCompactDisplay: Boolean, orientation: Int) {
  navigation(
    route = SUBSCRIPTIONS_GRAPH_ROUTE,
    startDestination = SUBSCRIPTIONS_ROUTE
  ) {
    if (isCompactDisplay) subscriptions(orientation = orientation)
    else subscriptions(orientation = orientation)

    searchResults(route = SUBSCRIPTIONS_GRAPH_ROUTE, orientation)
  }
}

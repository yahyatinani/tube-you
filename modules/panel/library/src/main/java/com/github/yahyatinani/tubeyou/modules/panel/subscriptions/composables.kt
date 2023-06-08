package com.github.yahyatinani.tubeyou.modules.panel.subscriptions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.yahyatinani.tubeyou.modules.core.keywords.SUBSCRIPTIONS_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.SUBSCRIPTIONS_ROUTE
import com.github.yahyatinani.tubeyou.modules.panel.common.search.searchPanel

fun NavGraphBuilder.subscriptions(orientation: Int) {
  composable(route = SUBSCRIPTIONS_ROUTE) {
    Surface(modifier = Modifier.fillMaxSize()) {
      Text(text = "TODO: subscriptions")
    }
  }
}

fun NavGraphBuilder.subsGraph(
  isCompactDisplay: Boolean,
  orientation: Int,
  thumbnailHeight: Dp
) {
  navigation(
    route = SUBSCRIPTIONS_GRAPH_ROUTE,
    startDestination = SUBSCRIPTIONS_ROUTE
  ) {
    if (isCompactDisplay) subscriptions(orientation = orientation)
    else subscriptions(orientation = orientation)

    searchPanel(
      route = SUBSCRIPTIONS_GRAPH_ROUTE,
      orientation = orientation,
      thumbnailHeight = thumbnailHeight
    )
  }
}

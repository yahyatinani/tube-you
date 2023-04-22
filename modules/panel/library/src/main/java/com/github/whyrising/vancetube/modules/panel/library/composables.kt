package com.github.whyrising.vancetube.modules.panel.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.whyrising.vancetube.modules.core.keywords.LIBRARY_GRAPH_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.LIBRARY_ROUTE
import com.github.whyrising.vancetube.modules.panel.common.searchResults

fun NavGraphBuilder.library(orientation: Int) {
  composable(
    route = LIBRARY_ROUTE
  ) {
    Surface(modifier = Modifier.fillMaxSize()) {
      Text(text = "TODO: library")
    }
  }
}

fun NavGraphBuilder.libraryGraph(
  isCompactDisplay: Boolean,
  orientation: Int,
  thumbnailHeight: Dp
) {
  navigation(
    route = LIBRARY_GRAPH_ROUTE,
    startDestination = LIBRARY_ROUTE
  ) {
    if (isCompactDisplay) library(orientation = orientation)
    else library(orientation = orientation)

    searchResults(
      route = LIBRARY_GRAPH_ROUTE,
      orientation = orientation,
      thumbnailHeight = thumbnailHeight
    )
  }
}

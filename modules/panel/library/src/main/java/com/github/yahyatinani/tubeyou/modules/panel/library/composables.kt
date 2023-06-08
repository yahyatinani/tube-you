package com.github.yahyatinani.tubeyou.modules.panel.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.yahyatinani.tubeyou.modules.core.keywords.LIBRARY_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.LIBRARY_ROUTE
import com.github.yahyatinani.tubeyou.modules.panel.common.search.searchPanel

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

    searchPanel(
      route = LIBRARY_GRAPH_ROUTE,
      orientation = orientation,
      thumbnailHeight = thumbnailHeight
    )
  }
}

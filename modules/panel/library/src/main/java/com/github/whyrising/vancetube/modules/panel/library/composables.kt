package com.github.whyrising.vancetube.modules.panel.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.library(orientation: Int) {
  composable(
    route = "LIBRARY_ROUTE"
  ) {
    Surface(modifier = Modifier.fillMaxSize()) {
      Text(text = "TODO: library")
    }
  }
}

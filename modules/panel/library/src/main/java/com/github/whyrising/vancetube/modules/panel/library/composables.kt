package com.github.whyrising.vancetube.modules.panel.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.whyrising.vancetube.modules.core.keywords.library

fun NavGraphBuilder.library(orientation: Int) {
  composable(
    route = library.route.toString()
  ) {
    Surface(modifier = Modifier.fillMaxSize()) {
      Text(text = "TODO: library")
    }
  }
}

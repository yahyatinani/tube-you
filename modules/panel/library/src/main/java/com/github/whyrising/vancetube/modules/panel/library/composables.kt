package com.github.whyrising.vancetube.modules.panel.library

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.github.whyrising.vancetube.modules.core.keywords.library
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.library(orientation: Int) {
  composable(
    route = library.route.toString()
  ) {
    Surface(modifier = Modifier.fillMaxSize()) {
      Text(text = "TODO: library")
    }
  }
}

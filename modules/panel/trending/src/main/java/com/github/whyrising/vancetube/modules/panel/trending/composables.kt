package com.github.whyrising.vancetube.modules.panel.trending

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.github.whyrising.vancetube.modules.core.keywords.trends
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.trending(orientation: Int) {
  composable(route = trends.route.toString()) {
    Surface(modifier = Modifier.fillMaxSize()) {
      Text(text = "TODO: trends")
    }
  }
}

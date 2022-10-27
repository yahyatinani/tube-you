package com.github.whyrising.vancetube.modules.panel.subscriptions

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.github.whyrising.vancetube.modules.core.keywords.subscriptions
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.subscriptions(orientation: Int) {
  composable(route = subscriptions.route.toString()) {
    Surface(modifier = Modifier.fillMaxSize()) {
      Text(text = "TODO: subscriptions")
    }
  }
}

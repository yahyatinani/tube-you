package com.github.whyrising.vancetube.subscriptions

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.github.whyrising.vancetube.ui.anim.enterAnimation
import com.github.whyrising.vancetube.ui.anim.exitAnimation
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.subscriptions(animOffsetX: Int, orientation: Int) {
  composable(
    route = subscriptions.route.toString(),
    exitTransition = { exitAnimation(targetOffsetX = -animOffsetX) },
    popEnterTransition = { enterAnimation(initialOffsetX = -animOffsetX) }
  ) {
    Surface(modifier = Modifier.fillMaxSize()) {
      Text(text = "TODO: subscriptions")
    }
  }
}

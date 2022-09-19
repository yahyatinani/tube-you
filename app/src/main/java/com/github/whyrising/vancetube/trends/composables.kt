package com.github.whyrising.vancetube.trends

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.github.whyrising.vancetube.base.db.NavigationItemState
import com.github.whyrising.vancetube.ui.anim.enterAnimation
import com.github.whyrising.vancetube.ui.anim.exitAnimation
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.trending(animOffSetX: Int, orientation: Int) {
  composable(
    route = NavigationItemState.Trending.route,
    exitTransition = { exitAnimation(targetOffsetX = -animOffSetX) },
    popEnterTransition = { enterAnimation(initialOffsetX = -animOffSetX) }
  ) {
    Surface(modifier = Modifier.fillMaxSize()) {
      Text(text = "TODO: trends")
    }
  }
}

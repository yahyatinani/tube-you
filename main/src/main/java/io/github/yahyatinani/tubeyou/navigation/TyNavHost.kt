package io.github.yahyatinani.tubeyou.navigation

import android.os.Bundle
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.HOME_GRAPH_ROUTE
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.homeGraph
import io.github.yahyatinani.tubeyou.modules.feature.library.youGraph
import io.github.yahyatinani.tubeyou.modules.feature.subscriptions.subscriptionsGraph
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.navigation.searchScreen

private val animationSpec = tween<Float>(100)

@Composable
fun TyNavHost(
  navController: NavHostController,
  modifier: Modifier = Modifier,
  startDestination: String = HOME_GRAPH_ROUTE,
  isCompact: Boolean,
  orientation: Int
) {
  NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = modifier,
    enterTransition = { fadeIn(animationSpec = animationSpec) },
    exitTransition = { fadeOut(animationSpec = animationSpec) }
  ) {
    val nestedGraphs: NavGraphBuilder.(String) -> Unit = { rootGraphRoute ->
      searchScreen(
        rootGraphRoute = rootGraphRoute,
        orientation = orientation
      )
    }
    homeGraph(
      isCompact = isCompact,
      orientation = orientation,
      nestedGraphs = nestedGraphs
    )
    subscriptionsGraph(nestedGraphs = nestedGraphs)
    youGraph(nestedGraphs = nestedGraphs)
  }
}

internal object NavStateSaver : Saver<Bundle, Bundle> {
  override fun restore(value: Bundle): Bundle? = value

  override fun SaverScope.save(value: Bundle): Bundle? = when {
    value.isEmpty -> null
    else -> value
  }
}

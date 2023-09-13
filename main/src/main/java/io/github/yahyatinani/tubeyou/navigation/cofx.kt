package io.github.yahyatinani.tubeyou.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.RegCofx

@Composable
fun RegNavCofx(navController: NavHostController) {
  RegCofx(common.start_destination) { cofx ->
    cofx.assoc(
      common.start_destination,
      navController.graph.findStartDestination().id
    )
  }

  RegCofx(common.prev_top_nav_route) { cofx ->
    val route = BackStack.pop(currentDestination(navController))
    cofx.assoc(common.prev_top_nav_route, route)
  }

  RegCofx(common.is_top_backstack_empty) { cofx ->
    cofx.assoc(
      common.is_top_backstack_empty,
      BackStack.queue.size <= 1
    )
  }
}

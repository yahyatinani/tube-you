package io.github.yahyatinani.tubeyou.fx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.RegFx
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.HOME_GRAPH_ROUTE
import io.github.yahyatinani.y.core.get
import kotlinx.coroutines.launch

fun currentDestination(navController: NavController) = navController
  .currentDestination?.hierarchy?.drop(1)?.first()?.route

object BackStack {
  val queue = ArrayDeque<String>().apply { add(HOME_GRAPH_ROUTE) }

  fun contains(to: String?) = queue.subList(1, queue.size).contains(to)

  fun addDistinct(currentDestination: String?) {
    if (
      currentDestination != null &&
      !contains(currentDestination)
    ) {
      queue.add(currentDestination)
    }
  }

  fun remove(destination: String) {
    if (contains(destination)) {
      queue.removeAt(queue.lastIndexOf(destination))
    }
  }

  /**
   * Must be called before navigating to a new destination.
   *
   * @return the last backstack route that was removed.
   */
  fun pop(currentDestination: String?): String {
    if (queue.size > 1) {
      if (queue.last() == currentDestination) {
        queue.removeLast()
      }
    }

    val lastBackStackRoute = queue.last()
    if (queue.size > 1) queue.removeLast()
    return lastBackStackRoute
  }
}

@Composable
fun RegNavFx(navController: NavHostController) {
  val scope = rememberCoroutineScope()

  RegFx(common.navigate_to) { destination ->
    val toRoute = get<String>(destination, common.destination)!!
    val navOptions = get<NavOptions>(destination, common.navOptions)

    if (navOptions != null) {
      BackStack.addDistinct(currentDestination(navController))
      BackStack.remove(toRoute)
    }
    scope.launch {
      navController.navigate(toRoute, navOptions)
    }
  }

  RegFx(common.back_press_top_nav) { prevDestinationRoute ->
    scope.launch {
      navController.navigate(prevDestinationRoute as String) {
        popUpTo(navController.graph.findStartDestination().id) {
          saveState = true
        }
        restoreState = true
      }
    }
  }

  RegFx(common.pop_back_stack) {
    scope.launch {
      navController.popBackStack()
    }
  }
}

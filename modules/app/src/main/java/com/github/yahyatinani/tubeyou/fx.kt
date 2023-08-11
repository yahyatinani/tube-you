package com.github.yahyatinani.tubeyou

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.navigate_to
import io.github.yahyatinani.recompose.RegFx
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
   * Must be called before navigation to a new destination.
   *
   * @return the last backstack route that was removed.
   */
  fun pop(navController: NavController): String {
    if (queue.size > 1) {
      if (queue.last() == currentDestination(navController)) {
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

  RegFx(navigate_to) { destination ->
    val toRoute = get<String>(destination, common.destination)!!
    val navOptions = get<NavOptions>(destination, common.navOptions)

    if (navOptions != null) {
      val currentDestination = currentDestination(navController)
      BackStack.addDistinct(currentDestination)
      BackStack.remove(toRoute)
    }
    scope.launch {
      navController.navigate(toRoute, navOptions)
    }
  }

  RegFx(common.bottom_bar_back_press) {
    scope.launch {
      navController.navigate(BackStack.pop(navController)) {
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

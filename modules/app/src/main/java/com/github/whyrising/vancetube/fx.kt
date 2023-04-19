package com.github.whyrising.vancetube

import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions
import com.github.whyrising.recompose.regFx
import com.github.whyrising.vancetube.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.navigate_to
import com.github.whyrising.vancetube.modules.panel.common.SEARCH_ROUTE

object BackStack {
  val queue = ArrayDeque<String>().apply { add(HOME_GRAPH_ROUTE) }

  fun currentDestination(navController: NavController) = navController
    .currentDestination?.hierarchy?.drop(1)?.first()?.route

  fun contains(to: String?) = queue.subList(1, queue.size).contains(to)

  fun addDistinct(currentDestination: String?) {
    if (currentDestination != null && !contains(currentDestination)) {
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

fun regAppFx(navController: NavController) {
  regFx(navigate_to) { toDestination ->
    if (toDestination is String && toDestination.contains(SEARCH_ROUTE)) {
      navController.navigate(toDestination)
    } else {
      BackStack.addDistinct(BackStack.currentDestination(navController))
      BackStack.remove(toDestination as String)
      navController.navigate(
        route = toDestination,
        navOptions = navOptions {
          popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
          }
          restoreState = true
        }
      )
    }
  }

  regFx(common.back_press) {
    navController.navigate(BackStack.pop(navController)) {
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
      }
      restoreState = true
    }
  }

  regFx(common.pop_back_stack) {
    navController.popBackStack()
  }
}

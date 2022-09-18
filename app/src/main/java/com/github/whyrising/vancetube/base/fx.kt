package com.github.whyrising.vancetube.base

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.whyrising.recompose.regFx
import com.github.whyrising.vancetube.base.db.NavigationItemState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun regBaseFx(navController: NavHostController) {
  regFx(base.navigate_to) { destination ->
    val route = destination.toString()
    runBlocking(Dispatchers.Main.immediate) {
      when {
        destination is NavigationItemState -> navController.navigate(route) {
          // Pop up to the start destination of the graph to
          // avoid building up a large stack of destinations
          // on the back stack as users select items
          popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
          }
          // Avoid multiple copies of the same destination when
          // reselecting the same item
          launchSingleTop = true
          // Restore state when reselecting a previously selected item
//          restoreState = true // Fixme: this flag breaks go_top_list
        }
        route == base.go_back.name -> navController.popBackStack()
        else -> navController.navigate(route)
      }
    }
  }
}

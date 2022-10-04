package com.github.whyrising.vancetube.base

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.whyrising.recompose.regFx
import com.github.whyrising.vancetube.modules.core.keywords.base.navigate_to
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun isBottomNav(destination: Any?): Boolean = navItems[destination] != null

fun regBaseFx(navController: NavHostController) =
  regFx(navigate_to) { destination ->
    destination as String
    val navigationAction: () -> Unit = if (isBottomNav(destination)) {
      {
        navController.navigate(destination) {
          // Pop up to the start destination of the graph to
          // avoid building up a large stack of destinations
          // on the back stack as users select items
          popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
          }
          // Avoid multiple copies of the same destination when
          // re-selecting the same item
          launchSingleTop = true
          // Restore state when re-selecting a previously selected item
          // restoreState = true // Fixme: this flag breaks go_top_list
        }
      }
    } else {
      TODO()
    }
//      base.go_back.name -> {
//        { navController.popBackStack() }
//      }
//      else -> {
//        { navController.navigate(route) }
//      }

    runBlocking(Dispatchers.Main.immediate) { navigationAction() }
  }

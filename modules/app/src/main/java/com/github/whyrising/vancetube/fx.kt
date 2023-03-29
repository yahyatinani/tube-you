package com.github.whyrising.vancetube

import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.regFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.navigate_to
import com.github.whyrising.y.core.get

fun popBackQueueNavOptions(
  navController: NavHostController,
  destinationRoute: String
) = navOptions {
  val lastNavRoute = navController.currentBackStackEntry?.destination?.route
  if (destinationRoute != lastNavRoute) { // clean duplicates in backQueue
    val iterator = navController.backQueue.iterator().apply {
      next() // skip null, index 0.
      next() // skip home aka starting point, index 1.
    }
    while (iterator.hasNext()) {
      if (iterator.next().destination.route == destinationRoute) {
        iterator.remove()
      }
    }
  }
}

fun regCommonFx(navController: NavHostController) {
  regFx(navigate_to) { navigation ->
    val destination = get<String>(navigation, common.destination)!!
//    val navOptions = get<NavOptions>(navigation, common.navOptions)

    navController.navigate(
      route = destination,
      navOptions = popBackQueueNavOptions(navController, destination)
    )
  }

  regCofx(common.current_back_stack_id) { coeffects ->
    val id = navController.currentBackStackEntry?.destination?.id
    if (id != null) coeffects.assoc(common.current_back_stack_id, id)
    else coeffects
  }
}

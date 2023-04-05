package com.github.whyrising.vancetube

import android.os.Bundle
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
  if (destinationRoute != lastNavRoute) {
    // skip first (null) and second (start route) indexes.
    val iterator = navController.backQueue.listIterator(index = 2)
    while (iterator.hasNext()) {
      if (iterator.next().destination.route == destinationRoute) {
        iterator.remove()
      }
    }
  }
}

private var navControllerState: Bundle? = null

fun regCommonFx(navController: NavHostController) {
  regFx(navigate_to) { navigation ->
    val destination = get<String>(navigation, common.destination)!!
//    val navOptions = get<NavOptions>(navigation, common.navOptions)

    navController.navigate(
      route = destination,
      navOptions = popBackQueueNavOptions(navController, destination)
    )
  }

  regFx(id = ":save-nav-state") { _ ->
    navControllerState = navController.saveState()
  }

  regFx(id = ":restore-nav-state") { _ ->
    navController.restoreState(navControllerState)
  }

  // -- Co-effects

  regCofx(common.current_back_stack_id) { coeffects ->
    val id = navController.currentBackStackEntry?.destination?.id
    if (id != null) coeffects.assoc(common.current_back_stack_id, id)
    else coeffects
  }
}

package com.github.whyrising.vancetube

import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.regFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.navigate_to
import com.github.whyrising.y.core.get

private fun removeStartDestinationIfDuplicated(navController: NavController) {
  if (navController.currentBackStackEntry?.destination?.id ==
    navController.graph.startDestinationId
  ) navController.backQueue.removeAt(navController.backQueue.size - 1)
}

fun popBackQueueNavOptions(
  navController: NavController,
  destinationRoute: String
) = navOptions {
  if (navController.backQueue.size <= 2) return@navOptions

  removeStartDestinationIfDuplicated(navController)

  if (destinationRoute == "search_query") return@navOptions

  val iterator = navController.backQueue.listIterator(index = 2)
  while (iterator.hasNext()) {
    if (iterator.next().destination.route == destinationRoute) {
      iterator.remove()
    }
  }
}

fun regGlobalFx(navController: NavController) {
  regFx(navigate_to) { destination ->
    //    val navOptions = get<NavOptions>(navigation, common.navOptions)
    when (destination) {
      common.go_back -> {
        println("sdfljsdlfjsdj")
        navController.popBackStack()
      }
      else -> navController.navigate(
        route = destination as String,
        navOptions = popBackQueueNavOptions(navController, destination)
      )
    }
  }

  // -- Co-effects

  regCofx(common.current_back_stack_id) { coeffects ->
    val id = navController.currentBackStackEntry?.destination?.id
    if (id != null) coeffects.assoc(common.current_back_stack_id, id)
    else coeffects
  }
}

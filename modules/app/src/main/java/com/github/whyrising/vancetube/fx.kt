package com.github.whyrising.vancetube

import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.recompose.regFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.navigate_to
import com.github.whyrising.y.core.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun regCommonFx(navController: NavHostController) {
  regFx(navigate_to) { navigation ->
    val destination = get<String>(navigation, common.destination)!!
    val navOptions = get<NavOptions>(navigation, common.navOptions)

//     if base.go_back.name -> {
//        { navController.popBackStack() }
//      }

    runBlocking(Dispatchers.Main.immediate) {
      navController.navigate(destination, navOptions)
    }
  }

  regCofx(common.current_back_stack_id) { coeffects ->
    val id = navController.currentBackStackEntry?.destination?.id
    if (id != null) coeffects.assoc(common.current_back_stack_id, id)
    else coeffects
  }
}

package com.github.whyrising.vancetube

import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.github.whyrising.recompose.regFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.navigate_to
import com.github.whyrising.y.core.getFrom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun isNavBarDestination(destination: Any?) = navItems[destination] != null

fun regCommonFx(navController: NavHostController) =
  regFx(navigate_to) { navigation ->
    val destination = getFrom<Any, String>(navigation, common.destination)!!
    val navOptions = getFrom<Any, NavOptions>(navigation, common.navOptions)

//     if base.go_back.name -> {
//        { navController.popBackStack() }
//      }

    runBlocking(Dispatchers.Main.immediate) {
      navController.navigate(destination, navOptions)
    }
  }

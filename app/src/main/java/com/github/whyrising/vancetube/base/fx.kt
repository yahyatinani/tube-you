package com.github.whyrising.vancetube.base

import androidx.navigation.NavHostController
import com.github.whyrising.recompose.regFx

fun regBaseFx(navController: NavHostController) {
  regFx(base.navigate) {
    when (val route = "$it") {
      base.go_back.name -> navController.popBackStack()
      else -> navController.navigate(route)
    }
  }
}

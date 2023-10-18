package io.github.yahyatinani.tubeyou.navigation

import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.github.yahyatinani.tubeyou.ui.TyMain

const val MAIN_ROUTE = "main_route"

fun NavGraphBuilder.mainScreen(
  navController: NavHostController,
  windowSizeClass: WindowSizeClass
) {
  composable(
    route = MAIN_ROUTE,
    enterTransition = { slideInVertically { -it / 10 } },
    exitTransition = { fadeOut() }
  ) {
    TyMain(navController, windowSizeClass)
  }
}

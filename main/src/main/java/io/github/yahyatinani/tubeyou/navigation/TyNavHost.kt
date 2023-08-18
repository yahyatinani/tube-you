package io.github.yahyatinani.tubeyou.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.HOME_GRAPH_ROUTE
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.homeGraph
import io.github.yahyatinani.tubeyou.modules.feature.library.youGraph
import io.github.yahyatinani.tubeyou.modules.feature.subscriptions.subscriptionsGraph

@Composable
fun TyNavHost(
  navController: NavHostController,
  modifier: Modifier = Modifier,
  startDestination: String = HOME_GRAPH_ROUTE
) {
  NavHost(
    navController = navController,
    startDestination = startDestination,
    modifier = modifier
  ) {
    homeGraph()
    subscriptionsGraph()
    youGraph()
  }
}

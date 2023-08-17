package io.github.yahyatinani.tubeyou.db

import androidx.compose.runtime.Immutable
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.HOME_GRAPH_ROUTE
import io.github.yahyatinani.tubeyou.navigation.TopLevelNavItems

@Immutable
data class TyAppState(
  val activeTopLevelRoute: String,
  val topLevelBackHandlerEnabled: Boolean
)

val defaultAppState = TyAppState(
  activeTopLevelRoute = HOME_GRAPH_ROUTE,
  topLevelBackHandlerEnabled = false
)

val navItems: List<TopLevelNavItems> = TopLevelNavItems.values().asList()

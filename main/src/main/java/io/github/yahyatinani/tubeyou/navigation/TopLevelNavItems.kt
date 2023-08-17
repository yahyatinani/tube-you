package io.github.yahyatinani.tubeyou.navigation

import io.github.yahyatinani.tubeyou.R
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.HOME_GRAPH_ROUTE
import io.github.yahyatinani.tubeyou.modules.feature.library.LIBRARY_GRAPH_ROUTE
import io.github.yahyatinani.tubeyou.modules.feature.subscriptions.SUBSCRIPTIONS_GRAPH_ROUTE

enum class TopLevelNavItems(
  val label: Int,
  val selectedIcon: Int,
  val unselectedIcon: Int,
  val route: String
) {
  Home(
    label = R.string.nav_item_label_home,
    selectedIcon = R.drawable.ic_filled_home,
    unselectedIcon = R.drawable.ic_outlined_home,
    route = HOME_GRAPH_ROUTE
  ),
  Subscriptions(
    label = R.string.nav_item_label_subs,
    selectedIcon = R.drawable.ic_filled_subs,
    unselectedIcon = R.drawable.ic_outlined_subs,
    route = SUBSCRIPTIONS_GRAPH_ROUTE
  ),
  Library(
    label = R.string.nav_item_label_library,
    selectedIcon = R.drawable.ic_filled_library,
    unselectedIcon = R.drawable.ic_outlined_library,
    route = LIBRARY_GRAPH_ROUTE
  )
}

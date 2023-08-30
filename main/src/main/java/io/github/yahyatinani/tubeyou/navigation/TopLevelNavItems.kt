package io.github.yahyatinani.tubeyou.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.AccountCircle
import io.github.yahyatinani.tubeyou.R
import io.github.yahyatinani.tubeyou.modules.feature.home.navigation.HOME_GRAPH_ROUTE
import io.github.yahyatinani.tubeyou.modules.feature.library.YOU_GRAPH_ROUTE
import io.github.yahyatinani.tubeyou.modules.feature.subscriptions.SUBSCRIPTIONS_GRAPH_ROUTE

enum class TopLevelNavItems(
  val label: Int,
  val selectedIcon: Any,
  val unselectedIcon: Any,
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
  You(
    label = R.string.nav_item_label_you,
    selectedIcon = Icons.Default.AccountCircle,
    unselectedIcon = Icons.Outlined.AccountCircle,
    route = YOU_GRAPH_ROUTE
  )
}

val topLevelNavItems = TopLevelNavItems.entries

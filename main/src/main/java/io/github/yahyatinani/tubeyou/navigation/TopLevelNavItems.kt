package io.github.yahyatinani.tubeyou.navigation

import com.github.yahyatinani.tubeyou.modules.designsystem.icon.TyIcons
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
    selectedIcon = TyIcons.HomeFilled,
    unselectedIcon = TyIcons.HomeOutlined,
    route = HOME_GRAPH_ROUTE
  ),
  Subscriptions(
    label = R.string.nav_item_label_subs,
    selectedIcon = TyIcons.SubsFilled,
    unselectedIcon = TyIcons.SubsOutlined,
    route = SUBSCRIPTIONS_GRAPH_ROUTE
  ),
  You(
    label = R.string.nav_item_label_you,
    selectedIcon = TyIcons.LibraryFilled,
    unselectedIcon = TyIcons.LibraryOutlined,
    route = YOU_GRAPH_ROUTE
  )
}

val topLevelNavItems = TopLevelNavItems.entries

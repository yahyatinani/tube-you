package io.github.yahyatinani.tubeyou.navigation

import io.github.yahyatinani.tubeyou.R

enum class MainNavItems(
  val label: Int,
  val selectedIcon: Int,
  val unselectedIcon: Int,
  val route: String
) {
  Home(
    label = R.string.nav_item_label_home,
    selectedIcon = R.drawable.ic_filled_home,
    unselectedIcon = R.drawable.ic_outlined_home,
    route = "home"
  ),
  Subscriptions(
    label = R.string.nav_item_label_subs,
    selectedIcon = R.drawable.ic_filled_subs,
    unselectedIcon = R.drawable.ic_outlined_subs,
    route = "subscriptions"
  ),
  Library(
    label = R.string.nav_item_label_library,
    selectedIcon = R.drawable.ic_filled_library,
    unselectedIcon = R.drawable.ic_outlined_library,
    route = "library"
  )
}

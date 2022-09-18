package com.github.whyrising.vancetube.base.db

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.whyrising.vancetube.R

sealed class NavigationItemState(activeNavItem: String) {
  abstract val labelTextId: Int
  abstract val icon: ImageVector
  abstract val iconContentDescTextId: Int

  val isSelected: Boolean = activeNavItem == this.toString()

  /* Sub-classes */

  class Home(activeNavItem: String) : NavigationItemState(activeNavItem) {
    override val labelTextId: Int = R.string.nav_item_label_home
    override val iconContentDescTextId: Int = R.string.nav_item_desc_home
    override val icon: ImageVector = when {
      isSelected -> Icons.Filled.Home
      else -> Icons.Outlined.Home
    }

    override fun toString(): String = route

    companion object {
      const val route: String = "home_route"
    }
  }

  class Trending(activeNavItem: String) : NavigationItemState(activeNavItem) {
    override val labelTextId: Int = R.string.nav_item_label_trend
    override val iconContentDescTextId: Int = R.string.nav_item_desc_home
    override val icon: ImageVector = when {
      isSelected -> Icons.Filled.TrendingUp
      else -> Icons.Outlined.TrendingUp
    }

    override fun toString(): String = route

    companion object {
      const val route: String = "trending_route"
    }
  }

  class Subscriptions(activeNavItem: String) :
    NavigationItemState(activeNavItem) {
    override val labelTextId: Int = R.string.nav_item_label_subs
    override val iconContentDescTextId: Int = R.string.nav_item_desc_subs
    override val icon: ImageVector = when {
      isSelected -> Icons.Filled.Subscriptions
      else -> Icons.Outlined.Subscriptions
    }

    override fun toString(): String = route

    companion object {
      const val route: String = "subscriptions_route"
    }
  }

  class Library(activeNavItem: String) : NavigationItemState(activeNavItem) {
    override val labelTextId: Int = R.string.nav_item_label_library
    override val iconContentDescTextId: Int = R.string.nav_item_desc_library
    override val icon: ImageVector = when {
      isSelected -> Icons.Filled.PlaylistPlay
      else -> Icons.Outlined.PlaylistPlay
    }

    override fun toString(): String = route

    companion object {
      const val route: String = "library_route"
    }
  }
}

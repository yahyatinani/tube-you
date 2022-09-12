package com.github.whyrising.vancetube.base.db

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.whyrising.vancetube.R

sealed interface NavigationItemState {
  val isSelected: Boolean
  val labelTextId: Int
  val icon: ImageVector
  val contentDescTextId: Int

  data class Home(override val isSelected: Boolean = false) :
    NavigationItemState {
    override val labelTextId: Int = R.string.nav_item_label_home
    override val contentDescTextId: Int = R.string.nav_item_desc_home
    override val icon: ImageVector = when {
      isSelected -> Icons.Filled.Home
      else -> Icons.Outlined.Home
    }
  }

  data class Subscriptions(override val isSelected: Boolean = false) :
    NavigationItemState {
    override val labelTextId: Int = R.string.nav_item_label_subs
    override val contentDescTextId: Int = R.string.nav_item_desc_subs
    override val icon: ImageVector = when {
      isSelected -> Icons.Filled.PlayArrow
      else -> Icons.Outlined.PlayArrow
    }
  }

  data class Library(override val isSelected: Boolean = false) :
    NavigationItemState {
    override val labelTextId: Int = R.string.nav_item_label_library
    override val contentDescTextId: Int = R.string.nav_item_desc_library
    override val icon: ImageVector = when {
      isSelected -> Icons.Filled.List
      else -> Icons.Outlined.List
    }
  }
}
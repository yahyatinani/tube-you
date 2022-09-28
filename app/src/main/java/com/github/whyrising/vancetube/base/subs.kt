package com.github.whyrising.vancetube.base

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.TrendingUp
import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.regSubM
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.vancetube.R
import com.github.whyrising.vancetube.base.base.bottom_nav_items
import com.github.whyrising.vancetube.base.base.current_bottom_nav_panel
import com.github.whyrising.vancetube.base.base.is_backstack_available
import com.github.whyrising.vancetube.base.base.start_route
import com.github.whyrising.vancetube.home.home
import com.github.whyrising.vancetube.library.library
import com.github.whyrising.vancetube.subscriptions.subscriptions
import com.github.whyrising.vancetube.trends.trends
import com.github.whyrising.y.core.assoc
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentArrayMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.github.whyrising.y.core.util.m as m2

// TODO: decouple type from map?
val navItems: PersistentArrayMap<Any, IPersistentMap<Any, Any>> = m(
  "${home.route}" to m2(
    base.label_text_id, R.string.nav_item_label_home,
    base.icon_content_desc_text_id, R.string.nav_item_desc_home,
    base.is_selected, false,
    base.icon_variant, Icons.Filled.Home,
    base.icon, Icons.Outlined.Home
  ),
  "${trends.route}" to m2(
    base.label_text_id, R.string.nav_item_label_trend,
    base.icon_content_desc_text_id, R.string.nav_item_desc_trends,
    base.is_selected, false,
    base.icon_variant, Icons.Filled.TrendingUp,
    base.icon, Icons.Outlined.TrendingUp
  ),
  "${subscriptions.route}" to m2(
    base.label_text_id, R.string.nav_item_label_subs,
    base.icon_content_desc_text_id, R.string.nav_item_desc_subs,
    base.is_selected, false,
    base.icon_variant, Icons.Filled.Subscriptions,
    base.icon, Icons.Outlined.Subscriptions
  ),
  "${library.route}" to m2(
    base.label_text_id, R.string.nav_item_label_library,
    base.icon_content_desc_text_id, R.string.nav_item_desc_library,
    base.is_selected, false,
    base.icon_variant, Icons.Filled.Bookmarks,
    base.icon, Icons.Outlined.Bookmarks
  )
)

val regBaseSubs by lazy {
  regSub<AppDb, Boolean>(is_backstack_available) { db, _ ->
    db[is_backstack_available] as Boolean
  }

  regSub<AppDb, Any?>(current_bottom_nav_panel) { db, _ ->
    db[current_bottom_nav_panel]
  }

  regSub<AppDb, Any?>(start_route) { db, _ ->
    db[start_route]
  }

  regSubM(
    queryId = bottom_nav_items,
    signalsFn = {
      v(
        subscribe(v(start_route)),
        subscribe(v(current_bottom_nav_panel))
      )
    },
    computationFn = { (startRoute, currentRoute), _ ->
      val selectedItem = navItems[currentRoute ?: startRoute]!!
      assoc(
        navItems,
        (currentRoute ?: startRoute) to assoc(
          selectedItem,
          base.icon to selectedItem[base.icon_variant],
          base.is_selected to true
        )
      )
    }
  )
}

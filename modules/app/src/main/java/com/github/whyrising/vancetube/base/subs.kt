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
import com.github.whyrising.vancetube.modules.core.keywords.common.bottom_nav_items
import com.github.whyrising.vancetube.modules.core.keywords.common.current_bottom_nav_panel
import com.github.whyrising.vancetube.modules.core.keywords.common.icon
import com.github.whyrising.vancetube.modules.core.keywords.common.icon_content_desc_text_id
import com.github.whyrising.vancetube.modules.core.keywords.common.icon_variant
import com.github.whyrising.vancetube.modules.core.keywords.common.is_backstack_available
import com.github.whyrising.vancetube.modules.core.keywords.common.is_selected
import com.github.whyrising.vancetube.modules.core.keywords.common.label_text_id
import com.github.whyrising.vancetube.modules.core.keywords.common.start_route
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.library
import com.github.whyrising.vancetube.modules.core.keywords.subscriptions
import com.github.whyrising.vancetube.modules.core.keywords.trends
import com.github.whyrising.y.core.assoc
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentArrayMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.github.whyrising.y.core.util.m as m2

// TODO: decouple type from map?
val navItems: PersistentArrayMap<Any, IPersistentMap<Any, Any>> = m(
  home.route.toString() to m2(
    label_text_id, R.string.nav_item_label_home,
    icon_content_desc_text_id, R.string.nav_item_desc_home,
    is_selected, false,
    icon_variant, Icons.Filled.Home,
    icon, Icons.Outlined.Home
  ),
  trends.route.toString() to m2(
    label_text_id, R.string.nav_item_label_trend,
    icon_content_desc_text_id, R.string.nav_item_desc_trends,
    is_selected, false,
    icon_variant, Icons.Filled.TrendingUp,
    icon, Icons.Outlined.TrendingUp
  ),
  subscriptions.route.toString() to m2(
    label_text_id, R.string.nav_item_label_subs,
    icon_content_desc_text_id, R.string.nav_item_desc_subs,
    is_selected, false,
    icon_variant, Icons.Filled.Subscriptions,
    icon, Icons.Outlined.Subscriptions
  ),
  library.route.toString() to m2(
    label_text_id, R.string.nav_item_label_library,
    icon_content_desc_text_id, R.string.nav_item_desc_library,
    is_selected, false,
    icon_variant, Icons.Filled.Bookmarks,
    icon, Icons.Outlined.Bookmarks
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
          icon to selectedItem[icon_variant],
          is_selected to true
        )
      )
    }
  )
}

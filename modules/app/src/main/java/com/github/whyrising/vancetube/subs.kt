package com.github.whyrising.vancetube

import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.vancetube.modules.core.keywords.common.active_navigation_item
import com.github.whyrising.vancetube.modules.core.keywords.common.icon
import com.github.whyrising.vancetube.modules.core.keywords.common.icon_content_desc_text_id
import com.github.whyrising.vancetube.modules.core.keywords.common.icon_variant
import com.github.whyrising.vancetube.modules.core.keywords.common.is_backstack_available
import com.github.whyrising.vancetube.modules.core.keywords.common.is_selected
import com.github.whyrising.vancetube.modules.core.keywords.common.label_text_id
import com.github.whyrising.vancetube.modules.core.keywords.common.navigation_items
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
    icon_variant, R.drawable.ic_filled_home,
    icon, R.drawable.ic_outlined_home
  ),
  trends.route.toString() to m2(
    label_text_id, R.string.nav_item_label_trend,
    icon_content_desc_text_id, R.string.nav_item_desc_trends,
    is_selected, false,
    icon_variant, R.drawable.ic_filled_trend,
    icon, R.drawable.ic_outlined_trend
  ),
  subscriptions.route.toString() to m2(
    label_text_id, R.string.nav_item_label_subs,
    icon_content_desc_text_id, R.string.nav_item_desc_subs,
    is_selected, false,
    icon_variant, R.drawable.ic_filled_subs,
    icon, R.drawable.ic_outlined_subs
  ),
  library.route.toString() to m2(
    label_text_id, R.string.nav_item_label_library,
    icon_content_desc_text_id, R.string.nav_item_desc_library,
    is_selected, false,
    icon_variant, R.drawable.ic_filled_library,
    icon, R.drawable.ic_outlined_library
  )
)

val regCommonSubs = run {
  regSub<AppDb, Boolean>(is_backstack_available) { db, _ ->
    db[is_backstack_available] as Boolean
  }

  regSub<AppDb, Any?>(active_navigation_item) { db, _ ->
    db[active_navigation_item]
  }

  regSub<String, Any>(
    queryId = navigation_items,
    signalsFn = { subscribe(v(active_navigation_item)) },
    initialValue = m<Any, Any>(),
    computationFn = { activeNavigationItem, _, _ ->
      val selectedItem = navItems[activeNavigationItem]!!
      assoc(
        navItems,
        activeNavigationItem to assoc(
          selectedItem,
          icon to selectedItem[icon_variant],
          is_selected to true
        )
      )
    }
  )
}

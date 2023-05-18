package com.github.yahyatinani.tubeyou

import com.github.whyrising.recompose.regSub
import com.github.whyrising.y.core.assoc
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentArrayMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.LIBRARY_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.SUBSCRIPTIONS_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.active_navigation_item
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.icon
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.icon_content_desc_text_id
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.icon_variant
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_backstack_available
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_backstack_empty
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_selected
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.label_text_id
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.navigation_items
import com.github.whyrising.y.core.util.m as m2

// TODO: decouple type from map?
val navItems: PersistentArrayMap<Any, IPersistentMap<Any, Any>> = m(
  HOME_GRAPH_ROUTE to m2(
    label_text_id, R.string.nav_item_label_home,
    icon_content_desc_text_id, R.string.nav_item_desc_home,
    is_selected, false,
    icon_variant, R.drawable.ic_filled_home,
    icon, R.drawable.ic_outlined_home
  ),
  SUBSCRIPTIONS_GRAPH_ROUTE to m2(
    label_text_id, R.string.nav_item_label_subs,
    icon_content_desc_text_id, R.string.nav_item_desc_subs,
    is_selected, false,
    icon_variant, R.drawable.ic_filled_subs,
    icon, R.drawable.ic_outlined_subs
  ),
  LIBRARY_GRAPH_ROUTE to m2(
    label_text_id, R.string.nav_item_label_library,
    icon_content_desc_text_id, R.string.nav_item_desc_library,
    is_selected, false,
    icon_variant, R.drawable.ic_filled_library,
    icon, R.drawable.ic_outlined_library
  )
)

fun activate(selectedItem: IPersistentMap<Any, Any>) = assoc(
  selectedItem,
  icon to selectedItem[icon_variant],
  is_selected to true
)

fun navigationItems(activeNavigationItem: Any) = assoc(
  navItems,
  activeNavigationItem to activate(navItems[activeNavigationItem]!!)
)

fun regAppSubs() {
  regSub(is_backstack_available, key = is_backstack_available)

  regSub(active_navigation_item, key = active_navigation_item)

  regSub(
    queryId = navigation_items,
    initialValue = m<Any, Any>(),
    v(active_navigation_item),
    computationFn = ::navigationItems
  )

  regSub(is_backstack_empty, key = is_backstack_empty)
}

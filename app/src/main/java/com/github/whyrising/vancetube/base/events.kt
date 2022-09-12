package com.github.whyrising.vancetube.base

import com.github.whyrising.recompose.fx.FxIds
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.base.base.bottom_nav_items
import com.github.whyrising.vancetube.base.base.set_backstack_status
import com.github.whyrising.vancetube.base.db.NavigationItemState
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.map
import com.github.whyrising.y.core.v

typealias AppDb = IPersistentMap<Any, Any>

fun regBaseEventHandlers() {
  regEventFx(base.navigate_to) { _, (_, destination) ->
    m(FxIds.fx to v(v(base.navigate_to, (destination as Enum<*>).name)))
  }

  regEventDb<AppDb>(set_backstack_status) { db, (_, flag) ->
    db.assoc(base.is_backstack_available, flag)
  }

  regEventDb<AppDb>(base.select_bottom_nav_item) { db, (_, destination) ->
    if ((destination as NavigationItemState).isSelected)
      return@regEventDb db

    // TODO: Navigate to destination panel.
    db.assoc(
      bottom_nav_items,
      map<NavigationItemState, NavigationItemState>(db[bottom_nav_items]) {
        if (it == destination || it.isSelected) it.toggleSelection()
        else it
      }
    )
  }
}

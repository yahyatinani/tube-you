package com.github.whyrising.vancetube.base

import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.vancetube.base.base.bottom_nav_items
import com.github.whyrising.vancetube.base.base.current_battom_nav_panel
import com.github.whyrising.vancetube.base.base.is_backstack_available
import com.github.whyrising.vancetube.base.db.NavigationItemState
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.v

fun regBaseSubs() {
  regSub<AppDb, Boolean>(is_backstack_available) { db, _ ->
    db[is_backstack_available] as Boolean
  }

  regSub<AppDb, Any>(current_battom_nav_panel) { db, _ ->
    db[current_battom_nav_panel]!!
  }

  regSub<String, List<NavigationItemState>>(
    queryId = bottom_nav_items,
    signalsFn = { subscribe(v(current_battom_nav_panel)) },
    computationFn = { currentPanelRoute, _ ->
      listOf(
        NavigationItemState.Home(activeNavItem = currentPanelRoute),
        NavigationItemState.Trending(activeNavItem = currentPanelRoute),
        NavigationItemState.Subscriptions(activeNavItem = currentPanelRoute),
        NavigationItemState.Library(activeNavItem = currentPanelRoute)
      )
    }
  )
}

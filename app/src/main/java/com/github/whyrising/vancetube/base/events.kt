package com.github.whyrising.vancetube.base

import com.github.whyrising.recompose.fx.FxIds.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.base.base.bottom_nav_items
import com.github.whyrising.vancetube.base.base.navigate_to
import com.github.whyrising.vancetube.base.base.set_backstack_status
import com.github.whyrising.vancetube.base.db.NavigationItemState
import com.github.whyrising.vancetube.home.getAppDb
import com.github.whyrising.vancetube.home.home
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

typealias AppDb = IPersistentMap<Any, Any>

fun isBottomBarDestination(destination: Any) =
  destination == NavigationItemState.Home.route ||
    destination == NavigationItemState.Subscriptions.route ||
    destination == NavigationItemState.Library.route

fun regBaseEventHandlers() {
  regEventFx(navigate_to) { _, (_, destination) ->
    m(fx to v(v(navigate_to, destination)))
  }

  regEventDb<AppDb>(set_backstack_status) { db, (_, flag) ->
    db.assoc(base.is_backstack_available, flag)
  }

  regEventDb<AppDb>(base.select_bottom_nav_item) { db, (_, destination) ->
    if (!isBottomBarDestination(destination)) {
      return@regEventDb db
    }

    val bottomNavItems = db[bottom_nav_items] as List<NavigationItemState>
    db.assoc(
      bottom_nav_items,
      bottomNavItems.map { navItem ->
        val navItemRoute = navItem.toString()
        val isDestination = navItemRoute == destination

        val shouldToggleSelection = when {
          navItem.isSelected -> !isDestination // unselect nav item
          else -> isDestination // select nav item
        }

        if (shouldToggleSelection) navItem.toggleSelection()
        else navItem
      }
    )
  }

  regEventFx(base.select_bottom_nav_item) { cofx, (_, destination) ->
    if (!isBottomBarDestination(destination)) {
      return@regEventFx m()
    }

    // TODO: maybe save the current destination/panel in AppDp to save running
    //  this when new destination is same as current. Then you get rid of that
    //  var down below.
    val appDb = getAppDb(cofx)
    val bottomNavItems = appDb[bottom_nav_items] as List<NavigationItemState>
    var goTopList = false
    val newAppDb = appDb.assoc(
      bottom_nav_items,
      bottomNavItems.map { navItem ->
        val navItemRoute = navItem.toString()
        val isDestination = navItemRoute == destination

        val shouldToggleSelection = when {
          navItem.isSelected -> {
            if (isDestination) goTopList = true // FIXME: maybe refactor this?
            !isDestination // unselect nav item
          }
          else -> isDestination // select nav item
        }

        if (shouldToggleSelection) navItem.toggleSelection()
        else navItem
      }
    )

    m(
      db to newAppDb,
      fx to v(v(if (goTopList) home.go_top_list else null))
    )
  }
}

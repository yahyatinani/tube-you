package com.github.whyrising.vancetube

import androidx.navigation.navOptions
import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.FxIds.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.active_navigation_item
import com.github.whyrising.vancetube.modules.core.keywords.common.current_back_stack_id
import com.github.whyrising.vancetube.modules.core.keywords.common.is_online
import com.github.whyrising.vancetube.modules.core.keywords.common.navigate_to
import com.github.whyrising.vancetube.modules.core.keywords.common.set_backstack_status
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.library
import com.github.whyrising.vancetube.modules.panel.home.getAppDb
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getFrom
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

typealias AppDb = IPersistentMap<Any, Any>

val regCommonEvents = run {
  regEventDb<Any>(
    id = common.initialise,
    interceptors = v(injectCofx(home.fsm), injectCofx(":is_online"))
  ) { db, _ ->
    // FIXME: Use merge(m1,m2) after implementing it in y library.
    defaultDb.assoc(home.panel, getFrom(db, home.panel)!!)
  }

  regEventFx(
    id = common.initialise,
    interceptors = v(injectCofx(home.fsm), injectCofx(is_online))
  ) { cofx, _ ->
    val isOnline = cofx[is_online]!! as Boolean
    val startingRoute = if (isOnline) home.route else library.route
    m<Any, Any>(
      db to defaultDb
        .assoc(active_navigation_item, startingRoute.toString())
        .assoc(home.panel, getFrom(cofx[db], home.panel)!!)
    )
  }

  regEventDb<AppDb>(set_backstack_status) { db, (_, flag) ->
    db.assoc(common.is_backstack_available, flag)
  }

  // TODO: rethink this event handler
  regEventDb<AppDb>(active_navigation_item) { db, (_, destination) ->
    if (navItems[destination] != null) {
      db.assoc(active_navigation_item, destination)
    } else db
  }

  regEventFx(navigate_to) { _, (_, destination) ->
    m<Any, Any>(fx to v(v(navigate_to, m(common.destination to destination))))
  }

  regEventFx(
    id = common.on_nav_item_click,
    interceptors = v(injectCofx(current_back_stack_id))
  ) { cofx, (_, destination) ->
    val appDb = getAppDb(cofx)
    if (appDb[active_navigation_item] == destination) {
      // TODO: Use one fx for all panels to scroll up by overriding reg fx
      m(fx to v(v(home.go_top_list)))
    } else {
      // TODO: Set active_panel to active_navigation_item
      m<Any, Any>(
        db to appDb.assoc(active_navigation_item, destination),
        fx to v(
          v(
            navigate_to,
            m(
              common.destination to destination,
              common.navOptions to navOptions {
                popUpTo(cofx[current_back_stack_id] as Int) {
                  saveState = true
                }
                launchSingleTop = true
              }
            )
          )
        )
      )
    }
  }
}

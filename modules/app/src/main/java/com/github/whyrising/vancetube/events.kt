package com.github.whyrising.vancetube

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.BuiltInFx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.active_navigation_item
import com.github.whyrising.vancetube.modules.core.keywords.common.current_back_stack_id
import com.github.whyrising.vancetube.modules.core.keywords.common.is_online
import com.github.whyrising.vancetube.modules.core.keywords.common.is_search_bar_active
import com.github.whyrising.vancetube.modules.core.keywords.common.navigate_to
import com.github.whyrising.vancetube.modules.core.keywords.common.set_backstack_status
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.library
import com.github.whyrising.vancetube.modules.panel.common.appDbBy
import com.github.whyrising.vancetube.modules.panel.common.letIf
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

typealias AppDb = IPersistentMap<Any, Any>

val regCommonEvents = run {
  regEventFx(
    id = common.initialize,
    interceptors = v(injectCofx(is_online))
  ) { cofx, _ ->
    val isOnline = cofx[is_online]!! as Boolean
    val startingRoute = if (isOnline) home.route else library.route
    m<Any, Any>(
      db to defaultDb.assoc(active_navigation_item, startingRoute.toString())
    )
  }

  regEventDb<AppDb>(set_backstack_status) { db, (_, flag) ->
    db.assoc(common.is_backstack_available, flag)
  }

  regEventDb<AppDb>(id = is_search_bar_active) { db, (_, flag) ->
    db.assoc(is_search_bar_active, flag)
  }

  regEventDb<AppDb>(id = ":show_search_bar") { db, _ ->
    val sb = m(":query" to "", ":suggestions" to v<String>())
    when (db[active_navigation_item]) {
      home.route.toString() -> {
        assocIn(db, l(home.panel, ":home/search_bar"), sb)
      }

      else -> {
        TODO()
      }
    }
  }

  regEventDb<AppDb>(id = ":hide_search_bar") { db, _ ->
    when (db[active_navigation_item]) {
      home.route.toString() -> {
        // TODO: use dissoc
        db.assoc(
          home.panel,
          (db[home.panel] as IPersistentMap<Any?, *>).dissoc(":home/search_bar")
        )
      }

      else -> {
        TODO()
      }
    }
  }

  // TODO: rethink this event handler
  regEventDb<AppDb>(active_navigation_item) { db, (_, destination) ->
    db.letIf(navItems[destination] != null) {
      it.assoc(active_navigation_item, destination)
    }
  }

  regEventFx(navigate_to) { _, (_, destination) ->
    m<Any, Any>(
      BuiltInFx.fx to v(v(navigate_to, m(common.destination to destination)))
    )
  }

  regEventFx(
    id = common.on_click_nav_item,
    interceptors = v(injectCofx(current_back_stack_id))
  ) { cofx, (_, destination) ->
    // TODO: Set active_panel to active_navigation_item
    val appDb = appDbBy(cofx)
    m<Any, Any>(
      db to appDb.assoc(active_navigation_item, destination),
      BuiltInFx.fx to v(
        if (destination == appDb[active_navigation_item]) {
          // TODO: Use one fx for all panels to scroll up by overriding reg fx
          v(home.go_top_list)
        } else v(navigate_to, m(common.destination to destination))
      )
    )
  }
}

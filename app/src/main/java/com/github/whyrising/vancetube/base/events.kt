package com.github.whyrising.vancetube.base

import com.github.whyrising.recompose.fx.FxIds.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.base.base.current_bottom_nav_panel
import com.github.whyrising.vancetube.base.base.navigate_to
import com.github.whyrising.vancetube.base.base.set_backstack_status
import com.github.whyrising.vancetube.home.getAppDb
import com.github.whyrising.vancetube.home.home
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

typealias AppDb = IPersistentMap<Any, Any>

fun regBaseEventHandlers() {
  regEventFx(navigate_to) { _, (_, destination) ->
    m<Any, Any>(fx to v(v(navigate_to, destination)))
  }

  regEventDb<AppDb>(set_backstack_status) { db, (_, flag) ->
    db.assoc(base.is_backstack_available, flag)
  }

  regEventFx(base.on_bottom_nav_click) { cofx, (_, destination) ->
    val appDb = getAppDb(cofx)
    val currentNavPanel = appDb[current_bottom_nav_panel] as String

    if (currentNavPanel == "$destination") {
      // TODO: Use one fx for all panels to scroll up by overriding reg fx
      m(fx to v(v(home.go_top_list)))
    } else {
      m<Any, Any>(
        db to appDb.assoc(current_bottom_nav_panel, "$destination"),
        fx to v(v(navigate_to, destination))
      )
    }
  }
}

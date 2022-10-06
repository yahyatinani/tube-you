package com.github.whyrising.vancetube

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.FxIds.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.active_navigation_item
import com.github.whyrising.vancetube.modules.core.keywords.common.navigate_to
import com.github.whyrising.vancetube.modules.core.keywords.common.set_backstack_status
import com.github.whyrising.vancetube.modules.core.keywords.home
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
    interceptors = v(injectCofx(home.fsm))
  ) { db, _ ->
    // FIXME: Use merge(m1,m2) after implementing it in y library.
    defaultDb.assoc(home.panel, getFrom(db, home.panel)!!)
  }

  regEventFx(navigate_to) { _, (_, destination) ->
    m<Any, Any>(fx to v(v(navigate_to, destination)))
  }

  regEventDb<AppDb>(set_backstack_status) { db, (_, flag) ->
    db.assoc(common.is_backstack_available, flag)
  }

  regEventFx(common.on_nav_item_click) { cofx, (_, destination) ->
    // TODO: make sure this is a bottom navigation else skip
    val appDb = getAppDb(cofx)
    val currentNavPanel = appDb[active_navigation_item]

    if (currentNavPanel == "$destination") {
      // TODO: Use one fx for all panels to scroll up by overriding reg fx
      m(fx to v(v(home.go_top_list)))
    } else {
      m<Any, Any>(db to appDb.assoc(active_navigation_item, "$destination"))
    }
  }
}

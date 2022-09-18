package com.github.whyrising.vancetube.base

import com.github.whyrising.recompose.fx.FxIds.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.base.base.current_battom_nav_panel
import com.github.whyrising.vancetube.base.base.navigate_to
import com.github.whyrising.vancetube.base.base.set_backstack_status
import com.github.whyrising.vancetube.base.db.NavigationItemState
import com.github.whyrising.vancetube.home.getAppDb
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

typealias AppDb = IPersistentMap<Any, Any>

fun regBaseEventHandlers() {
  regEventFx(navigate_to) { cofx, (_, destination) ->
    m<Any, Any>()
      .let {
        if (destination is NavigationItemState) {
          val appDb = getAppDb(cofx)
          it.assoc(
            db,
            appDb.assoc(current_battom_nav_panel, destination.toString())
          )
        } else it
      }
      .assoc(fx, v(v(navigate_to, destination)))
  }

  regEventDb<AppDb>(set_backstack_status) { db, (_, flag) ->
    db.assoc(base.is_backstack_available, flag)
  }
}

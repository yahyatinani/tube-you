package com.github.whyrising.composetemplate.base

import com.github.whyrising.composetemplate.base.base.set_backstack_status
import com.github.whyrising.recompose.fx.FxIds
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

fun regBaseEventHandlers() {
  regEventFx(base.navigate) { _, (_, destination) ->
    m(FxIds.fx to v(v(base.navigate, (destination as Enum<*>).name)))
  }

  regEventDb<IPersistentMap<Any, Any>>(set_backstack_status) { db, (_, flag) ->
    db.assoc(base.is_backstack_available, flag)
  }
}

package com.github.whyrising.composetemplate.base

import com.github.whyrising.recompose.regSub
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get

fun regBaseSubs() {
  regSub<IPersistentMap<*, *>, Boolean>(base.is_backstack_available) { db, _ ->
    db[base.is_backstack_available] as Boolean
  }
}

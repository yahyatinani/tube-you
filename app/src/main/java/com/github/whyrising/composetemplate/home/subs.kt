package com.github.whyrising.composetemplate.home

import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.v

fun regHomeSubs() {
  regSub<IPersistentMap<Any, Any>, Int>(home.count) { db, _ ->
    db[home.count] as Int
  }

  regSub(
    queryId = home.btn_count_name,
    signalsFn = {
      subscribe<Int>(v(home.count))
    }
  ) { count, _ ->
    "Count ($count)"
  }
}

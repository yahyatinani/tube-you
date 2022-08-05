package com.github.whyrising.composetemplate.home

import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get

fun regHomeEvents() {
  regEventDb<IPersistentMap<Any, Any>>(home.inc_count) { db, _ ->
    db.assoc(home.count, (db[home.count] as Int).inc())
  }
}

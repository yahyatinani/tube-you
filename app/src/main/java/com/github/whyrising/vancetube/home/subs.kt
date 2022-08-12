package com.github.whyrising.vancetube.home

import com.github.whyrising.recompose.regSub
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.v

fun popularVideos(db: IPersistentMap<Any, Any>) =
  (db[home.panel] as IPersistentMap<Any, Any>)[home.popularVids]

fun regHomeSubs() {
  regSub<IPersistentMap<Any, Any>, Any>(home.popularVids) { db, _ ->
    popularVideos(db) ?: v<VideoMetadata>()
  }
}

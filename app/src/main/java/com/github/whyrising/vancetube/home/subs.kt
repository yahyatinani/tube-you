package com.github.whyrising.vancetube.home

import com.github.whyrising.recompose.regSub
import com.github.whyrising.vancetube.base.AppDb
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.v

fun homePanel(db: AppDb) = (db[home.panel] as AppDb)

fun popularVideos(db: AppDb): PersistentVector<VideoMetadata> =
  homePanel(db)[home.popularVids] as PersistentVector<VideoMetadata>? ?: v()

fun regHomeSubs() {
  regSub<AppDb, Any>(home.popularVids) { db, _ ->
    popularVideos(db)
  }
  regSub<AppDb, Any>(home.video_item_height) { db, _ ->
    homePanel(db)[home.video_item_height] ?: 180
  }
}

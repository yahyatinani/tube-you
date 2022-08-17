package com.github.whyrising.vancetube.home

import com.github.whyrising.recompose.regSub
import com.github.whyrising.vancetube.base.AppDb
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.v
import kotlinx.datetime.LocalTime

fun homePanel(db: AppDb) = (db[home.panel] as AppDb)

fun popularVideos(db: AppDb): PersistentVector<VideoMetadata> =
  homePanel(db)[home.popularVids] as PersistentVector<VideoMetadata>? ?: v()
fun formatSeconds(seconds: Int): String {
  val format = "%02d"
  val localTime = LocalTime.fromSecondOfDay(seconds)
  val s = format.format(localTime.second)
  return when (localTime.hour) {
    0 -> "${localTime.minute}:$s"
    else -> {
      val m = format.format(localTime.minute)
      "${localTime.hour}:$m:$s"
    }
  }
}

fun regHomeSubs() {
  regSub<AppDb, Any>(home.popularVids) { db, _ ->
    popularVideos(db)
  }
  regSub<AppDb, Any>(home.video_item_height) { db, _ ->
    homePanel(db)[home.video_item_height] ?: 180
  }
}

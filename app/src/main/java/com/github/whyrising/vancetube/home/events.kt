package com.github.whyrising.vancetube.home

import android.util.Log
import com.github.whyrising.recompose.cofx.Coeffects
import com.github.whyrising.recompose.fx.FxIds.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.base.AppDb
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.vancetube.home.home.load_popular_videos
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

fun getAppDb(cofx: Coeffects): AppDb = cofx[db] as AppDb

val regHomeEvents by lazy {
  Log.i("regHomeEvents", "init")
  regEventFx(load_popular_videos) { cofx, _ ->
    val appDb = getAppDb(cofx)
    if (appDb[home.panel] is HomePanelState.Loaded) {
      return@regEventFx m()
    }
    m(
      db to assocIn(appDb, l(home.panel), HomePanelState.Loading),
      fx to v(v(load_popular_videos, get(appDb, base.api)))
    )
  }

  regEventDb<AppDb>(id = home.set_popular_vids) { db, (_, vids) ->
    assocIn(db, l(home.panel), HomePanelState.Loaded(vids as List<VideoData>))
  }

  regEventFx(home.refresh) { cofx, (_, materialised) ->
    val appDb = getAppDb(cofx)
    val api = get(appDb, base.api)
    val newState = HomePanelState.Refreshing(
      (materialised as HomePanelState.Materialised).popularVideos
    )
    m(
      db to assocIn(appDb, l(home.panel), newState),
      fx to v(v(load_popular_videos, api))
    )
  }

  regEventFx(home.go_top_list) { cofx, _ ->
    val appDb = getAppDb(cofx)
    if (appDb[home.panel] !is HomePanelState.Loaded) {
      return@regEventFx m()
    }

    m(fx to v(v(home.go_top_list)))
  }
}

package com.github.whyrising.vancetube.home

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
  regEventFx(load_popular_videos) { cofx, _ ->
    val appDb = getAppDb(cofx)
    if ((appDb[home.panel] as HomeDb).state == States.Loaded) {
      return@regEventFx m()
    }
    m(
      db to assocIn(appDb, l(home.panel), HomeDb()),
      fx to v(v(load_popular_videos, get(appDb, base.api)))
    )
  }

  regEventDb<AppDb>(id = home.set_popular_vids) { db, (_, vids) ->
    assocIn(db, l(home.panel), HomeDb(States.Loaded, vids as List<VideoData>))
  }

  regEventFx(home.refresh) { cofx, _ ->
    val appDb = getAppDb(cofx)
    val api = get(appDb, base.api)
    val homeDb = (appDb[home.panel] as HomeDb).copy(state = States.Refreshing)
    m(
      db to assocIn(appDb, l(home.panel), homeDb),
      fx to v(v(load_popular_videos, api))
    )
  }

  regEventFx(home.go_top_list) { cofx, _ ->
    val appDb = getAppDb(cofx)
    if ((appDb[home.panel] as HomeDb).state == States.Loaded) {
      return@regEventFx m()
    }

    m(fx to v(v(home.go_top_list)))
  }
}

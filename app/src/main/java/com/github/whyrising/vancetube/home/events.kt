package com.github.whyrising.vancetube.home

import com.github.whyrising.recompose.fx.FxIds.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.base.AppDb
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

fun regHomeEvents() {
  regEventFx(home.load) { cofx, _ ->
    val appDb = cofx[db] as IPersistentMap<Any, Any>
    val api = get(appDb, base.api)
    m(
      db to assocIn(appDb, l(home.panel), HomePanelState.Loading),
      fx to v(v(home.load, api))
    )
  }

  regEventDb<AppDb>(id = home.set_popular_vids) { db, (_, vids) ->
    assocIn(db, l(home.panel), HomePanelState.Loaded(vids as List<VideoData>))
  }

  regEventFx(home.refresh) { cofx, (_, materialised) ->
    val appDb = cofx[db] as IPersistentMap<Any, Any>
    val api = get(appDb, base.api)
    val newState = HomePanelState.Refreshing(
      (materialised as HomePanelState.Materialised).popularVideos
    )
    m(
      db to assocIn(appDb, l(home.panel), newState),
      fx to v(v(home.load, api))
    )
  }
}

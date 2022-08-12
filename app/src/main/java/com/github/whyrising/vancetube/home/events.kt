package com.github.whyrising.vancetube.home

import com.github.whyrising.recompose.fx.FxIds
import com.github.whyrising.recompose.ids.recompose
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

fun regHomeEvents() {
  regEventFx(home.set_popular_vids) { cofx, (_, vids) ->
    val appDb = cofx[recompose.db] as IPersistentMap<Any, Any>
    m(recompose.db to assocIn(appDb, l(home.panel, home.popularVids), vids))
  }

  regEventFx(home.get_popular_vids) { cofx, event ->
    val appDb = cofx[recompose.db] as IPersistentMap<Any, Any>
    val api = get(appDb, base.api)
    m(FxIds.fx to v(v(home.get_popular_vids, api)))
  }
}

package com.github.whyrising.vancetube.home

import com.github.whyrising.recompose.fx.FxIds
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
  regEventFx(home.set_popular_vids) { cofx, (_, vids) ->
    val appDb = (cofx[db] as IPersistentMap<Any, Any>)
      .let { assocIn(it, l(home.panel, home.is_loading), false) }
      .let { assocIn(it, l(home.panel, home.is_refreshing), false) }
      .let { assocIn(it, l(home.panel, home.popular_vids), vids) }
    m(db to appDb)
  }

  regEventFx(home.get_popular_vids) { cofx, _ ->
    val appDb = cofx[db] as IPersistentMap<Any, Any>
    val api = get(appDb, base.api)
    m(fx to v(v(home.get_popular_vids, api)))
  }

  regEventDb<AppDb>(home.is_loading) { db, (_, flag) ->
    assocIn(db, l(home.panel, home.is_loading), flag)
  }

  regEventFx(home.refresh) { cofx, _ ->
    val appDb = cofx[db] as IPersistentMap<Any, Any>
    m(
      db to assocIn(appDb, l(home.panel, home.is_refreshing), true),
      fx to v(v(FxIds.dispatch, v(home.get_popular_vids)))
    )
  }
}

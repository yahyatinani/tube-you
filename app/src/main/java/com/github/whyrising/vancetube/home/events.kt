package com.github.whyrising.vancetube.home

import android.util.Log
import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.FxIds
import com.github.whyrising.recompose.ids.recompose.db
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
    val appDb = cofx[db] as IPersistentMap<Any, Any>
    m(db to assocIn(appDb, l(home.panel, home.popular_vids), vids))
  }

  regEventFx(home.get_popular_vids) { cofx, event ->
    val appDb = cofx[db] as IPersistentMap<Any, Any>
    val api = get(appDb, base.api)
    m(FxIds.fx to v(v(home.get_popular_vids, api)))
  }

  regEventFx(
    id = home.video_item_height,
    interceptors = v(injectCofx(home.video_item_height))
  ) { cofx, _ ->
    val appDb = cofx[db] as AppDb
    val height = cofx[home.video_item_height] as Int
    Log.w("height", "$height")
    m(db to assocIn(appDb, l(home.panel, home.video_item_height), height))
  }
}

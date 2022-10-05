package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.recompose.cofx.Coeffects
import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.fx.FxIds.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.modules.core.keywords.base
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.home.go_top_list
import com.github.whyrising.vancetube.modules.core.keywords.home.load_popular_videos
import com.github.whyrising.vancetube.modules.core.keywords.home.panel
import com.github.whyrising.vancetube.modules.core.keywords.home.popular_vids
import com.github.whyrising.vancetube.modules.core.keywords.home.refresh
import com.github.whyrising.vancetube.modules.core.keywords.home.set_popular_vids
import com.github.whyrising.vancetube.modules.core.keywords.home.state
import com.github.whyrising.y.core.assoc
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getFrom
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

fun getAppDb(cofx: Coeffects): AppDb = cofx[db] as AppDb

val regHomeEvents by lazy {
  regEventFx(
    id = load_popular_videos,
    interceptors = v(injectCofx(home.fsm))
  ) { cofx, _ ->
    val appDb = getAppDb(cofx)
    val effects = m<Any, Any>(db to appDb)
    if (homeCurrentState(appDb) == States.Loaded) {
      return@regEventFx effects
    }

    effects.assoc(fx, v(v(load_popular_videos, appDb[base.api])))
  }

  regEventDb<AppDb>(
    id = set_popular_vids,
    interceptors = v(injectCofx(home.fsm))
  ) { db, (_, videos) ->
    assocIn(db, l(panel, popular_vids), videos)
  }

  regEventFx(
    id = refresh,
    interceptors = v(injectCofx(home.fsm))
  ) { cofx, _ ->
    val appDb = getAppDb(cofx)
    m(
      db to appDb,
      fx to v(v(load_popular_videos, get(appDb, base.api)))
    )
  }

  regEventFx(go_top_list) { cofx, _ ->
    val appDb = getAppDb(cofx)
    if (homeCurrentState(appDb) != States.Loaded) {
      return@regEventFx m()
    }

    m(fx to v(v(go_top_list)))
  }
}

package com.github.whyrising.vancetube.home

import com.github.whyrising.recompose.cofx.Coeffects
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.fx.FxIds.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.base.AppDb
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.vancetube.home.home.load_popular_videos
import com.github.whyrising.vancetube.home.home.refresh
import com.github.whyrising.vancetube.home.home.set_popular_vids
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

val homeStateMachine = m<Any?, Any>(
  null to m(load_popular_videos to States.Loading),
  States.Loading to m(
    set_popular_vids to States.Loaded,
    ":error" to States.Failed
  ),
  States.Loaded to m(
    refresh to States.Refreshing,
    load_popular_videos to States.Loaded
  ),
  States.Refreshing to m(
    set_popular_vids to States.Loaded,
    ":error" to States.Failed
  ),
  States.Failed to m(load_popular_videos to States.Loading)
)

fun nextState(fsm: Map<*, *>, currentState: States?, transition: Any): States? {
  // TODO: implement getIn<K,V>(map, ks):K?
  return (fsm[currentState] as Map<*, States>)[transition]
}

fun updateToNextState(db: AppDb, event: Any): AppDb {
  val homeDb = db[home.panel] as HomeDb
  val nextState = nextState(homeStateMachine, homeDb.state, event)
  return if (nextState != null)
    assocIn(db, l(home.panel), homeDb.copy(state = nextState)) as AppDb
  else db
}

fun handleNextState(db: AppDb, event: Event): AppDb = event.let { (id) ->
  updateToNextState(db, id)
}

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

  regEventDb<AppDb>(id = set_popular_vids) { db, (_, vids) ->
    assocIn(db, l(home.panel), HomeDb(States.Loaded, vids as List<VideoData>))
  }

  regEventFx(refresh) { cofx, _ ->
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

package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.recompose.cofx.Coeffects
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.fx.FxIds.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.modules.core.keywords.base
import com.github.whyrising.vancetube.modules.core.keywords.home.go_top_list
import com.github.whyrising.vancetube.modules.core.keywords.home.load_popular_videos
import com.github.whyrising.vancetube.modules.core.keywords.home.panel
import com.github.whyrising.vancetube.modules.core.keywords.home.popular_vids
import com.github.whyrising.vancetube.modules.core.keywords.home.refresh
import com.github.whyrising.vancetube.modules.core.keywords.home.set_popular_vids
import com.github.whyrising.vancetube.modules.core.keywords.home.state
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.PersistentArrayMap
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

enum class States {
  Loading,
  Refreshing,
  Loaded,
  Failed,
}

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

fun nextState(
  fsm: Map<Any?, Any>,
  currentState: States?,
  transition: Any
): Any? = getIn(fsm, l(currentState, transition))

fun updateToNextState(db: AppDb, event: Any): AppDb {
  val currentState = homeCurrentState(db)
  val nextState = nextState(homeStateMachine, currentState, event)
  return if (nextState != null) {
    assocIn(db, l(panel, state), nextState) as AppDb
  } else db
}

fun handleNextState(db: AppDb, event: Event): AppDb = event.let { (id) ->
  updateToNextState(db, id)
}

fun getAppDb(cofx: Coeffects): AppDb = cofx[db] as AppDb

fun homeCurrentState(appDb: AppDb) =
  getIn<States>(appDb, l(panel, state))

val regHomeEvents by lazy {
  regEventFx(id = load_popular_videos) { cofx, event ->
    val appDb = getAppDb(cofx)
    val newDb = handleNextState(appDb, event)
    val ret: PersistentArrayMap<Any, Any?> = m(db to newDb)

    if (homeCurrentState(appDb) == States.Loaded) {
      return@regEventFx ret
    }

    ret.assoc(fx, v(v(load_popular_videos, appDb[base.api])))
  }

  regEventDb<AppDb>(id = set_popular_vids) { db, (id, vids) ->
    val newDb = assocIn(db, l(panel, popular_vids), vids)
    updateToNextState(newDb as AppDb, id)
  }

  regEventFx(id = refresh) { cofx, event ->
    val appDb = getAppDb(cofx)
    m(
      db to handleNextState(appDb, event),
      fx to v(v(load_popular_videos, get(appDb, base.api)))
    )
  }

  regEventFx(go_top_list) { cofx, _ ->
    val appDb = getAppDb(cofx)
    if (homeCurrentState(appDb) == States.Loaded) {
      return@regEventFx m()
    }

    m(fx to v(v(go_top_list)))
  }
}

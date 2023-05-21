package com.github.yahyatinani.tubeyou.modules.panel.home

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.fx.BuiltInFx.dispatch
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.go_top_list
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.load_trending
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.States
import com.github.yahyatinani.tubeyou.modules.panel.common.States.Failed
import com.github.yahyatinani.tubeyou.modules.panel.common.States.Loaded
import com.github.yahyatinani.tubeyou.modules.panel.common.States.Loading
import com.github.yahyatinani.tubeyou.modules.panel.common.States.Refreshing
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import com.github.yahyatinani.tubeyou.modules.panel.common.ktor
import com.github.yahyatinani.tubeyou.modules.panel.common.letIf
import com.github.yahyatinani.tubeyou.modules.panel.common.search.Video
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

// -- Home FSM -----------------------------------------------------------------

val Home_Transitions = m<Any?, Any>(
  null to m(home.load to Loading),
  Loading to m(
    home.loading_is_done to Loaded,
    home.loading_failed to Failed
  ),
  Loaded to m(
    home.load to Loading,
    home.refresh to Refreshing,
    home.loading_failed to Failed
  ),
  Refreshing to m(
    home.loading_is_done to Loaded,
    home.loading_failed to Failed
  ),
  Failed to m(
    home.refresh to Loading,
    home.load to Loading,
    home.loading_is_done to Loaded
  )
)

fun homeCurrentState(appDb: AppDb) =
  getIn<States>(appDb, l(HOME_GRAPH_ROUTE, home.state, 0))

fun nextState(fsm: Map<Any?, Any>, currentState: States?, transition: Any) =
  getIn<Any?>(fsm, l(currentState, transition))

fun updateToNextState(db: AppDb, event: Any): AppDb {
  val currentState = homeCurrentState(db)
  val nextState = nextState(Home_Transitions, currentState, event)
  return db.letIf(nextState != null) {
    assocIn(it, l(HOME_GRAPH_ROUTE, home.state), v(nextState)) as AppDb
  }
}

fun handleNextState(db: AppDb, event: Event): AppDb = event.let { (id) ->
  updateToNextState(db, id)
}

// -- Registrations ------------------------------------------------------------

/**
 * Register all handlers of home FSM events.
 */
private fun fsmTriggers() {
  regEventFx(id = home.load) { cofx, e ->
    val newDb = handleNextState(appDbBy(cofx), e)
    m<Any, Any?>(db to newDb, fx to v(v(dispatch, v(load_trending))))
  }

  regEventDb<AppDb>(id = home.loading_is_done) { db, e ->
    val (_, videos) = e
    assocIn(handleNextState(db, e), l(HOME_GRAPH_ROUTE, home.state, 1), videos)
  }

  regEventFx(id = home.refresh) { cofx, e ->
    m(
      db to handleNextState(appDbBy(cofx), e),
      fx to v(v(dispatch, v(load_trending)))
    )
  }

  regEventDb<AppDb>(id = home.loading_failed) { db, e ->
    val error = e[1]
    assocIn(handleNextState(db, e), l(HOME_GRAPH_ROUTE, home.state, 1), error)
  }
}

fun regHomeEvents() {
  fsmTriggers()

  regEventFx(
    id = load_trending,
    interceptors = v(injectCofx(home.coroutine_scope))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    val popularVideosEndpoint = "${appDb[common.api_url]}/trending?region=US"
    m<Any, Any>(
      db to appDb,
      fx to v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to popularVideosEndpoint,
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[home.coroutine_scope],
            ktor.response_type_info to typeInfo<PersistentVector<Video>>(),
            ktor.on_success to v(home.loading_is_done),
            ktor.on_failure to v(home.loading_failed)
          )
        )
      )
    )
  }

  regEventFx(go_top_list) { _, _ ->
    m(fx to v(v(go_top_list)))
  }
}

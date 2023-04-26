package com.github.yahyatinani.tubeyou.modules.panel.home

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.fx.BuiltInFx
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
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.error
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.go_top_list
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.load
import com.github.yahyatinani.tubeyou.modules.core.keywords.home.popular_vids
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
  null to m(home.initialize to v(Loading, v(v(BuiltInFx.dispatch, v(load))))),
  Loading to m(home.loading_is_done to v(Loaded), error to v(Failed)),
  Loaded to m(home.refresh to v(Refreshing, v(v(BuiltInFx.dispatch, v(load))))),
  Refreshing to m(home.loading_is_done to v(Loaded), error to v(Failed)),
  Failed to m(home.initialize to v(Loading, v(v(BuiltInFx.dispatch, v(load)))))
)

fun homeCurrentState(appDb: AppDb): Any? =
  getIn<Any>(appDb, l(HOME_GRAPH_ROUTE, home.state))

fun nextState(
  fsm: Map<Any?, Any>,
  currentState: States?,
  transition: Any
): Any? = getIn(fsm, l(currentState, transition))

fun updateToNextState(db: AppDb, event: Any): AppDb {
  val currentState = get<States?>(homeCurrentState(db), 0)
  val nextState = nextState(Home_Transitions, currentState, event)
  return db.letIf(nextState != null) {
    assocIn(it, l(HOME_GRAPH_ROUTE, home.state), nextState) as AppDb
  }
}

fun handleNextState(db: AppDb, event: Event): AppDb = event.let { (id) ->
  updateToNextState(db, id)
}

fun fsmActionFx(state: Any?) = get<Any>(state, 1)

// -- Registration -------------------------------------------------------------

/**
 * Register all event handlers of FSM events.
 */
private fun fsmEvents() {
  regEventFx(
    id = home.initialize,
    interceptors = v(injectCofx(home.fsm_next_state))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    m<Any, Any?>(db to appDb, fx to fsmActionFx(homeCurrentState(appDb)))
  }

  regEventDb<AppDb>(
    id = home.loading_is_done,
    interceptors = v(injectCofx(home.fsm_next_state))
  ) { db, (_, videos) ->
    assocIn(db, l(HOME_GRAPH_ROUTE, popular_vids), videos)
  }

  regEventFx(
    id = home.refresh,
    interceptors = v(injectCofx(home.fsm_next_state))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    m(db to appDb, fx to fsmActionFx(homeCurrentState(appDb)))
  }
}

fun regHomeEvents() {
  fsmEvents()

  regEventDb<AppDb>(
    id = error,
    interceptors = v(injectCofx(home.fsm_next_state))
  ) { db, (_, e) ->
    assocIn(db, l(HOME_GRAPH_ROUTE, error), e)
  }

  regEventFx(
    id = load,
    interceptors = v(
      injectCofx(home.fsm_next_state),
      injectCofx(home.coroutine_scope)
    )
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    val popularVideosEndpoint = "${appDb[common.api_url]}/trending?region=CA"
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
            ktor.on_failure to v(error)
          )
        )
      )

    )
  }

  regEventFx(go_top_list) { _, _ ->
    m(fx to v(v(go_top_list)))
  }
}

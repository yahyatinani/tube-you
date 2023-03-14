package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.fx.FxIds
import com.github.whyrising.recompose.fx.FxIds.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.home.error
import com.github.whyrising.vancetube.modules.core.keywords.home.go_top_list
import com.github.whyrising.vancetube.modules.core.keywords.home.load
import com.github.whyrising.vancetube.modules.core.keywords.home.popular_vids
import com.github.whyrising.vancetube.modules.core.keywords.home.refresh
import com.github.whyrising.vancetube.modules.panel.common.AppDb
import com.github.whyrising.vancetube.modules.panel.common.States
import com.github.whyrising.vancetube.modules.panel.common.VideoData
import com.github.whyrising.vancetube.modules.panel.common.appDbBy
import com.github.whyrising.vancetube.modules.panel.common.ktor
import com.github.whyrising.vancetube.modules.panel.common.letIf
import com.github.whyrising.vancetube.modules.panel.common.nextState
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

// -- Home FSM -----------------------------------------------------------------

val Home_Transitions = m<Any?, Any>(
  null to m(
    home.initialize to v(
      States.Loaded,
//      v(v(FxIds.dispatch, v(load)))
    )
  ),
  States.Loading to m(
    home.loading_is_done to v(States.Loaded),
    error to v(States.Failed)
  ),
  States.Loaded to m(
    refresh to v(
      States.Refreshing,
      v(v(FxIds.dispatch, v(load)))
    )
  ),
  States.Refreshing to m(
    home.loading_is_done to v(States.Loaded),
    error to v(States.Failed)
  ),
  States.Failed to m(
    home.initialize to v(States.Loading, v(v(FxIds.dispatch, v(load))))
  )
)

fun homeCurrentState(appDb: AppDb): Any? =
  getIn<Any>(appDb, l(home.panel, home.state))

fun updateToNextState(db: AppDb, event: Any): AppDb {
  val currentState = get<States?>(homeCurrentState(db), 0)
  val nextState =
    nextState(Home_Transitions, currentState, event)
  return db.letIf(nextState != null) {
    assocIn(it, l(home.panel, home.state), nextState) as AppDb
  }
}

fun handleNextState(db: AppDb, event: Event): AppDb = event.let { (id) ->
  updateToNextState(db, id)
}

fun effectsByState(state: Any?) = get<Any>(state, 1)

// -- Registration -------------------------------------------------------------

val regHomeEvents = run {
  regEventFx(
    id = home.initialize,
    interceptors = v(injectCofx(home.fsm))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    m<Any, Any?>(db to appDb).assoc(fx, effectsByState(homeCurrentState(appDb)))
  }

  regEventDb<AppDb>(
    id = home.loading_is_done,
    interceptors = v(injectCofx(home.fsm))
  ) { db, (_, videos) ->
    assocIn(db, l(home.panel, popular_vids), videos)
  }

  regEventDb<AppDb>(
    id = error,
    interceptors = v(injectCofx(home.fsm))
  ) { db, (_, e) ->
    assocIn(db, l(home.panel, error), e)
  }

  regEventFx(
    id = load,
    interceptors = v(injectCofx(home.fsm), injectCofx(home.coroutine_scope))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    val popularVideosEndpoint = "${appDb[common.api_endpoint]}/popular?" +
      "fields=videoId,title,videoThumbnails,lengthSeconds,viewCount,author," +
      "publishedText,authorId"
    m<Any, Any>(db to appDb).assoc(
      fx,
      v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to popularVideosEndpoint,
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[home.coroutine_scope],
            ktor.response_type_info to typeInfo<PersistentVector<VideoData>>(),
            ktor.on_success to v(home.loading_is_done),
            ktor.on_failure to v(error)
          )
        )
      )
    )
  }

  regEventFx(
    id = refresh,
    interceptors = v(injectCofx(home.fsm))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    m(db to appDb, fx to effectsByState(homeCurrentState(appDb)))
  }

  regEventFx(go_top_list) { _, _ ->
    m(fx to v(v(go_top_list)))
  }
}

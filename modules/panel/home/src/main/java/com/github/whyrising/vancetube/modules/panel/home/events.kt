package com.github.whyrising.vancetube.modules.panel.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.fx.BuiltInFx
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
import com.github.whyrising.vancetube.modules.panel.common.bounce_fx
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
import kotlinx.serialization.Serializable

// -- Home FSM -----------------------------------------------------------------

val Home_Transitions = m<Any?, Any>(
  null to m(
    home.initialize to v(
      States.Loaded,
      v(v(BuiltInFx.dispatch, v(load)))
    )
  ),
  States.Loading to m(
    home.loading_is_done to v(States.Loaded),
    error to v(States.Failed)
  ),
  States.Loaded to m(
    refresh to v(
      States.Refreshing,
      v(v(BuiltInFx.dispatch, v(load)))
    )
  ),
  States.Refreshing to m(
    home.loading_is_done to v(States.Loaded),
    error to v(States.Failed)
  ),
  States.Failed to m(
    home.initialize to v(States.Loading, v(v(BuiltInFx.dispatch, v(load))))
  )
)

fun homeCurrentState(appDb: AppDb): Any? =
  getIn<Any>(appDb, l(home.panel, home.state))

fun updateToNextState(db: AppDb, event: Any): AppDb {
  val currentState = get<States?>(homeCurrentState(db), 0)
  val nextState = nextState(Home_Transitions, currentState, event)
  return db.letIf(nextState != null) {
    assocIn(it, l(home.panel, home.state), nextState) as AppDb
  }
}

fun handleNextState(db: AppDb, event: Event): AppDb = event.let { (id) ->
  updateToNextState(db, id)
}

fun effectsByState(state: Any?) = get<Any>(state, 1)

// -- Registration -------------------------------------------------------------

fun getRegHomeEvents() {
  regEventFx(
    id = home.initialize,
    interceptors = v(injectCofx(home.fsm))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    m<Any, Any?>(db to appDb).assoc(
      BuiltInFx.fx,
      effectsByState(homeCurrentState(appDb))
    )
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
      BuiltInFx.fx,
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
    m(db to appDb, BuiltInFx.fx to effectsByState(homeCurrentState(appDb)))
  }

  regEventFx(go_top_list) { _, _ ->
    m(BuiltInFx.fx to v(v(go_top_list)))
  }

  regEventDb<AppDb>(id = ":isActive") { db, (_, flag) ->
    assocIn(db, l(home.panel, ":home/search_bar", ":isActive"), flag)
  }

  @Immutable
  @Serializable
  data class Suggestions(
    val query: String,
    @Stable
    val suggestions: PersistentVector<String>
  )

  regEventDb<AppDb>(id = ":home/set-suggestions") { db, (_, suggestions) ->
    assocIn(
      db,
      l(home.panel, ":home/search_bar", ":suggestions"),
      (suggestions as Suggestions).suggestions
    )
  }

  regEventFx(
    id = ":search",
    interceptors = v(injectCofx(home.coroutine_scope))
  ) { cofx, (_, searchQuery) ->
    val sq = (searchQuery as String).replace(" ", "%20")
    val appDb = appDbBy(cofx)
    val suggestionsEndpoint =
      "${appDb[common.api_endpoint]}/search/suggestions?q=$sq"

    m<Any, Any>().assoc(
      BuiltInFx.fx,
      v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to suggestionsEndpoint,
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[home.coroutine_scope],
            ktor.response_type_info to typeInfo<Suggestions>(),
            ktor.on_success to v(":home/set-suggestions"),
            ktor.on_failure to v(error)
          )
        )
      )
    )
  }

  regEventFx(
    id = ":query",
    interceptors = v(injectCofx(home.coroutine_scope))
  ) { cofx, (_, searchQuery) ->
    val appDb = appDbBy(cofx)
    val newDb =
      assocIn(appDb, l(home.panel, ":home/search_bar", ":query"), searchQuery)

    m<Any, Any>(db to newDb).assoc(
      BuiltInFx.fx,
      v(
        v(
          common.dispatch_debounce,
          m(
            bounce_fx.id to ":search",
            bounce_fx.event to v(":search", searchQuery),
            bounce_fx.delay to 500
          )
        )
      )
    )
  }
}

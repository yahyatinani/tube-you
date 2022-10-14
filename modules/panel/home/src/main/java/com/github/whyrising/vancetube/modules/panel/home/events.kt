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
import com.github.whyrising.vancetube.modules.core.keywords.home.go_top_list
import com.github.whyrising.vancetube.modules.core.keywords.home.load_popular_videos
import com.github.whyrising.vancetube.modules.core.keywords.home.popular_vids
import com.github.whyrising.vancetube.modules.core.keywords.home.refresh
import com.github.whyrising.vancetube.modules.core.keywords.home.set_popular_vids
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
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.CoroutineScope

// -- Home FSM -----------------------------------------------------------------

val Home_State_Machine = m<Any?, Any>(
  null to m(common.initialise to States.Loading),
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

fun homeCurrentState(appDb: AppDb): States? =
  getIn<States>(appDb, l(home.panel, home.state))

fun updateToNextState(db: AppDb, event: Any): AppDb {
  val nextState = nextState(Home_State_Machine, homeCurrentState(db), event)
  return db.letIf(nextState != null) {
    assocIn(it, l(home.panel, home.state), nextState) as AppDb
  }
}

fun handleNextState(db: AppDb, event: Event): AppDb = event.let { (id) ->
  updateToNextState(db, id)
}

// -- Registration -------------------------------------------------------------

fun regHomeEvents(scope: CoroutineScope) {
  regEventDb<AppDb>(
    id = set_popular_vids,
    interceptors = v(injectCofx(home.fsm))
  ) { db, (_, videos) ->
    assocIn(db, l(home.panel, popular_vids), videos)
  }

  regEventFx(
    id = load_popular_videos,
    interceptors = v(injectCofx(home.fsm))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx)
    if (homeCurrentState(appDb) == States.Loaded) {
      return@regEventFx m()
    }

    m<Any, Any>(db to appDb).assoc(
      fx,
      v(
        v(
          ktor.http_fx,
          m(
            ktor.method to ktor.get,
            ktor.uri to "${appDb[common.api]}/popular?fields=videoId,title,videoThumbnails," +
              "lengthSeconds,viewCount,author,publishedText,authorId",
//            ktor.timeout to 8000,
            ktor.exec_scope to scope,
            ktor.response_type_info to typeInfo<PersistentVector<VideoData>>(),
            ktor.on_success to v(set_popular_vids),
            ktor.on_failure to v(":to-do") // TODO:
          )
        )
      )
    )
  }

  regEventFx(
    id = refresh,
    interceptors = v(injectCofx(home.fsm))
  ) { cofx, _ ->
    m(
      db to appDbBy(cofx),
      fx to v(v(FxIds.dispatch, v(load_popular_videos)))
    )
  }

  regEventFx(go_top_list) { cofx, _ ->
    val appDb = appDbBy(cofx)
    if (homeCurrentState(appDb) != States.Loaded) {
      return@regEventFx m()
    }

    m(fx to v(v(go_top_list)))
  }
}

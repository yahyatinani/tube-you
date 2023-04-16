package com.github.whyrising.vancetube.modules.panel.common

import com.github.whyrising.recompose.cofx.Coeffects
import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

enum class States { Loading, Refreshing, Loaded, Failed }

fun nextState(
  fsm: Map<Any?, Any>,
  currentState: States?,
  transition: Any
): Any? = getIn(fsm, l(currentState, transition))

fun appDbBy(cofx: Coeffects): AppDb = cofx[db] as AppDb

fun regCommonEvents() {
  regEventFx(
    id = ":search_suggestions",
    interceptors = v(injectCofx(home.coroutine_scope))
  ) { cofx, (_, searchQuery) ->
    val sq = (searchQuery as String).replace(" ", "%20")
    val appDb = appDbBy(cofx)
    val suggestionsEndpoint =
      "${appDb[common.api_endpoint]}/search/suggestions?q=$sq"

    m<Any, Any>().assoc(
      fx,
      v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to suggestionsEndpoint,
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[home.coroutine_scope],
            ktor.response_type_info to typeInfo<Suggestions>(),
            ktor.on_success to v(":set-suggestions"),
            ktor.on_failure to v(home.error)
          )
        )
      )
    )
  }

  regEventFx(
    id = ":search_query",
    interceptors = v(injectCofx(home.coroutine_scope))
  ) { cofx, (_, searchQuery) ->
    if ((searchQuery as String).isEmpty()) return@regEventFx m()

    val trim = searchQuery.trim()
    val sq = trim.replace(" ", "%20")
    val appDb = appDbBy(cofx)
    // TODO: &type=video, support a all types?
    val searchEndpoint = "${appDb[common.api_endpoint]}/search?q=$sq&type=video"
    val newDb = assocIn(
      appDb,
      l(appDb[common.active_navigation_item], common.search_bar, ":query"),
      trim
    )
    m<Any, Any>(
      db to newDb.assoc(common.is_search_bar_active, false),
      fx to v(
        v(common.navigate_to, "search_query"),
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to searchEndpoint,
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[home.coroutine_scope],
            ktor.response_type_info to typeInfo<PersistentVector<VideoData>>(),
            ktor.on_success to v(":set_search_results"),
            ktor.on_failure to v(home.error)
          )
        )
      )
    )
  }
}

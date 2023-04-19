package com.github.whyrising.vancetube.modules.panel.common

import com.github.whyrising.recompose.cofx.Coeffects
import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.api_endpoint
import com.github.whyrising.vancetube.modules.core.keywords.common.destination
import com.github.whyrising.vancetube.modules.core.keywords.common.search_bar
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.searchBar
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.IPersistentMap
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
    id = common.search_suggestions,
    interceptors = v(injectCofx(home.coroutine_scope))
  ) { cofx, (_, searchQuery) ->
    val sq = (searchQuery as String).replace(" ", "%20")
    val appDb = appDbBy(cofx)
    val suggestionsEndpoint = "${appDb[api_endpoint]}/search/suggestions?q=$sq"

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
            ktor.on_success to v(common.set_suggestions),
            ktor.on_failure to v(home.error)
          )
        )
      )
    )
  }

  regEventFx(
    id = common.search,
    interceptors = v(injectCofx(home.coroutine_scope))
  ) { cofx, (_, searchQuery) ->
    if ((searchQuery as String).isEmpty()) return@regEventFx m()

    val trimmedQuery = searchQuery.trim()
    val appDb = appDbBy(cofx)
    val activeTab = appDb[common.active_navigation_item]
    val sbVec = getIn<PersistentVector<Any>>(appDb, l(activeTab, search_bar))!!
    val sbIndex = sbVec.size - 1
    val fsb = (sbVec.last() as IPersistentMap<Any, Any>)
      .assoc(searchBar.query, trimmedQuery)
      .assoc(searchBar.search_id, sbIndex)

    val newDb =
      assocIn(appDb, l(activeTab, search_bar), sbVec.pop().conj(fsb))
        .assoc(common.is_search_bar_active, false)

    val restSearchQuery = trimmedQuery.replace(" ", "%20")
    // TODO: &type=video, support a all types?
    val searchEndpoint =
      "${appDb[api_endpoint]}/search?q=$restSearchQuery&type=video"
    m<Any, Any>(
      db to newDb,
      fx to v(
        v(common.navigate_to, m(destination to "$activeTab/$SEARCH_ROUTE")),
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to searchEndpoint,
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[home.coroutine_scope],
            ktor.response_type_info to typeInfo<PersistentVector<VideoData>>(),
            ktor.on_success to v(common.set_search_results, sbIndex),
            ktor.on_failure to v(home.error)
          )
        )
      )
    )
  }
}

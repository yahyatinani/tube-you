package com.github.yahyatinani.tubeyou.modules.panel.common.search

import com.github.yahyatinani.tubeyou.modules.core.keywords.common.api_url
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.get_search_results
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.get_search_suggestions
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.set_search_results
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.set_suggestions
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.panel.common.activeTab
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import io.github.yahyatinani.recompose.cofx.injectCofx
import io.github.yahyatinani.recompose.fsm.trigger
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.httpfx.ktor
import io.github.yahyatinani.recompose.ids.recompose.db
import io.github.yahyatinani.recompose.pagingfx.paging
import io.github.yahyatinani.recompose.regEventFx
import io.github.yahyatinani.y.core.collections.PersistentVector
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

fun regCommonEvents() {
  regEventFx(
    id = get_search_suggestions,
    interceptors = v(injectCofx(search.coroutine_scope))
  ) { cofx, (_, searchQuery) ->
    val sq = (searchQuery as String).replace(" ", "%20")

    m<Any, Any>(
      fx to v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to "${appDbBy(cofx)[api_url]}/suggestions?query=$sq",
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[search.coroutine_scope],
            ktor.response_type_info to typeInfo<PersistentVector<String>>(),
            ktor.on_success to v(
              search.panel_fsm,
              set_suggestions,
              searchQuery
            ),
            ktor.on_failure to v(":search/error")
          )
        )
      )
    )
  }

  regEventFx(
    id = get_search_results,
    interceptors = v(injectCofx(search.coroutine_scope))
  ) { cofx, (_, searchQuery) ->
    val sb = get<SearchBar>(searchQuery, search_bar)
    val sq = sb?.get(searchBar.query) as String?
    val handleResultsEvent = v(search.panel_fsm, set_search_results, sb)
    val api = appDbBy(cofx)[api_url]
    m<Any, Any>(
      fx to v(
        v(
          paging.fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to "$api/search?q=$sq&filter=all",
            ktor.timeout to 8000,
            "pageName" to "nextpage",
            "nextUrl" to "$api/nextpage/search?q=$sq&filter=all",
            "eventId" to get_search_results,
            ktor.coroutine_scope to cofx[search.coroutine_scope],
            ktor.response_type_info to typeInfo<SearchResponse>(),
            ktor.on_success to handleResultsEvent,
            "on_appending" to v(search.panel_fsm, "append"),
            ktor.on_failure to handleResultsEvent
          )
        )
      )
    )
  }

  regEventFx(id = search.panel_fsm) { cofx, e ->
    val appDb = appDbBy(cofx)
    trigger(
      searchPanelMachine,
      m(db to appDb),
      v(activeTab(appDb), search.panel_fsm),
      e.subvec(1, e.count)
    )
  }
}

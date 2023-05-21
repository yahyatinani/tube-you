package com.github.yahyatinani.tubeyou.modules.panel.common.search

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.BuiltInFx.dispatch
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.api_url
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_search_bar_active
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.back_press_search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.clear_search_input
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.get_search_results
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.get_search_suggestions
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.search_failed
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.set_search_results
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.set_suggestions
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.show_search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.stack
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.update_search_input
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.activeTab
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import com.github.yahyatinani.tubeyou.modules.panel.common.ktor
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

fun regFsmEvents() {
  regEventFx(id = show_search_bar) { cofx, _ ->
    sbFsm(appDbBy(cofx), show_search_bar)
  }

  regEventFx(id = update_search_input) { cofx, (_, searchQuery) ->
    sbFsm(appDbBy(cofx), update_search_input, searchQuery)
  }

  regEventFx(id = back_press_search) { cofx, _ ->
    sbFsm(appDbBy(cofx), back_press_search)
  }

  regEventFx(id = search.submit) { cofx, (_, searchQuery) ->
    if ((searchQuery as String).isEmpty()) return@regEventFx m()

    sbFsm(appDbBy(cofx), search.submit, searchQuery)
  }

  regEventFx(set_search_results) { cofx, (_, results) ->
    sbFsm(appDbBy(cofx), set_search_results, results)
  }

  regEventFx(search_failed) { cofx, (_, error) ->
    sbFsm(appDbBy(cofx), set_search_results, error)
  }

  regEventFx(id = clear_search_input) { cofx, _ ->
    m<Any, Any>(
      db to showSearchBar(appDbBy(cofx)),
      fx to v(v(dispatch, v(update_search_input, "")))
    )
    sbFsm(appDbBy(cofx), clear_search_input, "")
  }
}

fun regCommonEvents() {
  regFsmEvents()

  regEventDb<AppDb>(id = is_search_bar_active) { db, (_, flag) ->
    db.assoc(is_search_bar_active, flag)
  }

  regEventDb<AppDb>(id = set_suggestions) { db, (_, suggestions) ->
    val activeTab = activeTab(db)
    val searchBarFsm = searchBarFsm(db) ?: return@regEventDb db

    val searchBarStack = searchBarStack(searchBarFsm)

    val newSearchBarFsm = assocIn(
      searchBarFsm,
      l(stack, searchBarStack.count - 1, searchBar.suggestions),
      suggestions
    )
    setSbFsm(db, activeTab, newSearchBarFsm)
  }

  regEventFx(
    id = get_search_suggestions,
    interceptors = v(injectCofx(search.coroutine_scope))
  ) { cofx, (_, searchQuery) ->
    if ((searchQuery as String).isBlank() || searchQuery.isEmpty()) {
      return@regEventFx m<Any, Any>(
        fx to v(v(dispatch, v(set_suggestions, l<String>())))
      )
    }

    val sq = searchQuery.replace(" ", "%20")
    val appDb = appDbBy(cofx)
    val suggestionsEndpoint = "${appDb[api_url]}/suggestions?query=$sq"

    m<Any, Any>(
      fx to v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to suggestionsEndpoint,
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[search.coroutine_scope],
            ktor.response_type_info to typeInfo<PersistentVector<String>>(),
            ktor.on_success to v(set_suggestions),
            ktor.on_failure to v(":search/error")
          )
        )
      )
    )
  }

  regEventFx(
    id = get_search_results,
    interceptors = v(injectCofx(search.coroutine_scope))
  ) { cofx, (_, sq) ->
    m<Any, Any>(
      fx to v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to "${appDbBy(cofx)[api_url]}/search?q=$sq&filter=all",
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[search.coroutine_scope],
            ktor.response_type_info to typeInfo<SearchResponse>(),
            ktor.on_success to v(set_search_results),
            ktor.on_failure to v(search_failed)
          )
        )
      )
    )
  }
}

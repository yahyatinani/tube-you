package com.github.yahyatinani.tubeyou.modules.panel.common.search

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.BuiltInFx.dispatch
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.active_navigation_item
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_search_bar_active
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.navigate_to
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.search_suggestions
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.results
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.search_id
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import com.github.yahyatinani.tubeyou.modules.panel.common.bounce_fx
import com.github.yahyatinani.tubeyou.modules.panel.common.ktor
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

private fun removeSearchBar(
  appDb: AppDb,
  activeTab: Any?
) = getIn<IPersistentMap<Any, Any>>(appDb, l(activeTab))!!.dissoc(search_bar)

fun regCommonEvents() {
  regEventDb<AppDb>(id = is_search_bar_active) { db, (_, flag) ->
    db.assoc(is_search_bar_active, flag)
  }

  regEventDb<AppDb>(id = common.show_search_bar) { db, _ ->
    assocIn(db, l(db[active_navigation_item], search_bar), v(defaultSb))
      .assoc(is_search_bar_active, true)
  }

  regEventFx(
    id = search_suggestions,
    interceptors = v(injectCofx(":search/coroutine_scope"))
  ) { cofx, (_, searchQuery) ->
    val sq = (searchQuery as String).replace(" ", "%20")
    val appDb = appDbBy(cofx)
    val suggestionsEndpoint = "${appDb[common.api_url]}/suggestions?query=$sq"

    m<Any, Any>(
      fx to v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to suggestionsEndpoint,
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[":search/coroutine_scope"],
            ktor.response_type_info to typeInfo<PersistentVector<String>>(),
            ktor.on_success to v(common.set_suggestions),
            ktor.on_failure to v(home.error)
          )
        )
      )
    )
  }

  regEventFx(
    id = common.search,
    interceptors = v(injectCofx(":search/coroutine_scope"))
  ) { cofx, (_, searchQuery) ->
    if ((searchQuery as String).isEmpty()) return@regEventFx m()

    val trimmedQuery = searchQuery.trim()
    val appDb = appDbBy(cofx)
    val activeTab = appDb[active_navigation_item]
    val sbVec = getIn<PersistentVector<Any>>(appDb, l(activeTab, search_bar))!!
    val last = sbVec.last() as IPersistentMap<Any, Any>
    val prevSb = (if (sbVec.count > 1) sbVec.pop().last() else last)
      as IPersistentMap<Any, Any>

    val nsb = last.assoc(searchBar.query, trimmedQuery)
    val isDraftSb = last[results] == null
    val isSamePrevSearch = prevSb[searchBar.query] == trimmedQuery

    val newSbVec = when {
      isSamePrevSearch && isDraftSb && sbVec.count > 1 -> sbVec.pop().pop()
      else -> sbVec.pop()
    }
    val sbIndex = newSbVec.count
    val newDb = assocIn(
      appDb,
      l(activeTab, search_bar),
      newSbVec.conj(nsb.assoc(search_id, sbIndex))
    ).assoc(is_search_bar_active, false)

    val sq = trimmedQuery.replace(" ", "%20")
    val searchEndpoint = "${appDb[common.api_url]}/search?q=$sq&filter=all"
    val isOneSb = sbVec.count == 1
    m<Any, Any>(
      db to newDb,
      fx to v(
        if (isOneSb && !isDraftSb && isSamePrevSearch ||
          !isOneSb && isDraftSb && isSamePrevSearch
        ) {
          null
        } else {
          v(navigate_to, m(common.destination to "$activeTab/$SEARCH_ROUTE"))
        },
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to searchEndpoint,
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[":search/coroutine_scope"],
            ktor.response_type_info to typeInfo<SearchResponse>(),
            ktor.on_success to v(common.set_search_results, sbIndex),
            ktor.on_failure to v(":error")
          )
        )
      )
    )
  }

  regEventFx(
    id = common.search_input,
    interceptors = v(injectCofx(":search/coroutine_scope"))
  ) { cofx, (_, searchQuery) ->
    val appDb = appDbBy(cofx)
    val activeTab = appDb[active_navigation_item]
    val sbVec = getIn<PersistentVector<Any>>(appDb, l(activeTab, search_bar))!!
    val sb = sbVec.last() as IPersistentMap<Any, Any>
    val i = if (sb[search_id] != null) sbVec.count else sbVec.count - 1

    m<Any, Any>(
      db to assocIn(
        appDb,
        l(activeTab, search_bar, i, searchBar.query),
        searchQuery
      ),
      fx to v(
        v(
          common.dispatch_debounce,
          m(
            bounce_fx.id to search_suggestions,
            bounce_fx.event to v(search_suggestions, searchQuery),
            bounce_fx.delay to 500
          )
        )
      )
    )
  }

  regEventFx(common.back_press) { _, _ ->
    m<Any, Any>(fx to v(v(common.back_press)))
  }

  regEventDb<AppDb>(id = common.set_suggestions) { db, (_, suggestions) ->
    val activeTab = db[active_navigation_item]
    val sbVec = getIn<PersistentVector<IPersistentMap<Any, Any>>>(
      db,
      l(activeTab, search_bar)
    ) ?: return@regEventDb db

    val sb = sbVec.last().assoc(searchBar.suggestions, suggestions)

    assocIn(db, l(activeTab, search_bar), sbVec.pop().conj(sb))
  }

  regEventDb<AppDb>(
    id = common.set_search_results
  ) { db, (_, searchId, searchResults) ->
    val activeTab = db[active_navigation_item]
    val vec = getIn<PersistentVector<Any>>(db, l(activeTab, search_bar))

    if (vec == null || vec.count <= searchId as Int) return@regEventDb db

    assocIn(
      db,
      l(activeTab, search_bar, searchId, results),
      (searchResults as SearchResponse).items
    )
  }

  regEventFx(id = common.search_back_press) { cofx, _ ->
    val appDb = appDbBy(cofx)
    val isSearchBarActive = appDb[is_search_bar_active] as Boolean
    val activeTab = appDb[active_navigation_item]
    val sbVec = getIn<PersistentVector<Any>>(appDb, l(activeTab, search_bar))!!
    val sb = sbVec.last() as IPersistentMap<Any, Any>
    val searchDone = sb[search_id] != null

    if (isSearchBarActive && searchDone) {
      return@regEventFx m<Any, Any>(
        db to appDb.assoc(is_search_bar_active, false)
      )
    }

    val newSbVec = sbVec.pop()
    val effects = if (searchDone) v(common.pop_back_stack) else null
    if (newSbVec.isEmpty()) {
      return@regEventFx m<Any, Any>(
        db to assocIn(appDb, l(activeTab), removeSearchBar(appDb, activeTab)),
        fx to v(effects)
      )
    } else {
      m<Any, Any>(
        db to assocIn(appDb, l(activeTab, search_bar), newSbVec)
          .assoc(is_search_bar_active, false),
        fx to v(effects)
      )
    }
  }

  regEventFx(id = common.clear_search_input) { cofx, _ ->
    m<Any, Any>(
      db to appDbBy(cofx).assoc(is_search_bar_active, true),
      fx to v(v(dispatch, v(common.search_input, "")))
    )
  }
}

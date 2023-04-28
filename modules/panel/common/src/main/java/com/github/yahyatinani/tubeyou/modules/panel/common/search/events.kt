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
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_search_bar_active
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.navigate_to
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.search_stack
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.search_suggestions
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.results
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.search_id
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import com.github.yahyatinani.tubeyou.modules.panel.common.bounce_fx
import com.github.yahyatinani.tubeyou.modules.panel.common.getActiveTab
import com.github.yahyatinani.tubeyou.modules.panel.common.ktor
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

typealias SearchBar = IPersistentMap<Any, Any>
typealias SearchStack = PersistentVector<SearchBar>

private fun removeSearchBar(
  appDb: AppDb,
  activeTab: Any?
) = getIn<AppDb>(appDb, l(activeTab))!!.dissoc(search_stack)

private fun searchStack(
  appDb: AppDb,
  activeTab: Any?
): SearchStack? = getIn<SearchStack>(appDb, l(activeTab, search_stack))

fun swapTop(searchStack: SearchStack, searchBar: SearchBar): SearchStack =
  searchStack.pop().conj(searchBar)

fun top(searchStack: SearchStack) = searchStack.peek()

fun regCommonEvents() {
  regEventDb<AppDb>(id = is_search_bar_active) { db, (_, flag) ->
    db.assoc(is_search_bar_active, flag)
  }

  regEventDb<AppDb>(id = common.show_search_bar) { db, _ ->
    assocIn(db, l(getActiveTab(db), search_stack), v(defaultSb))
      .assoc(is_search_bar_active, true)
  }

  regEventDb<AppDb>(id = common.set_suggestions) { db, (_, suggestions) ->
    val activeTab = getActiveTab(db)
    val searchStack = searchStack(db, activeTab) ?: return@regEventDb db

    val newTop = top(searchStack)!!.assoc(searchBar.suggestions, suggestions)

    assocIn(db, l(activeTab, search_stack), swapTop(searchStack, newTop))
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
            ktor.on_failure to v(":search/error")
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
    val activeTab = getActiveTab(appDb)
    val searchStack = searchStack(appDb, activeTab)!!
    val top = top(searchStack)!!
    val i = when {
      top[search_id] != null -> searchStack.count
      else -> searchStack.count - 1
    }

    m<Any, Any>(
      db to assocIn(
        appDb,
        l(activeTab, search_stack, i, searchBar.query),
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

  regEventDb<AppDb>(
    id = common.set_search_results
  ) { db, (_, searchId, searchResults) ->
    val activeTab = getActiveTab(db)
    val searchStack = searchStack(db, activeTab)
    if (searchStack == null || searchStack.count <= searchId as Int) {
      return@regEventDb db
    }

    assocIn(
      db,
      l(activeTab, search_stack, searchId, results),
      (searchResults as SearchResponse).items
    )
  }

  regEventFx(
    id = common.search,
    interceptors = v(injectCofx(":search/coroutine_scope"))
  ) { cofx, (_, searchQuery) ->
    if ((searchQuery as String).isEmpty()) return@regEventFx m()

    val trimmedQuery = searchQuery.trim()
    val appDb = appDbBy(cofx)
    val activeTab = getActiveTab(appDb)
    val searchStack = searchStack(appDb, activeTab)!!
    val top = top(searchStack)!!
    val prevSb = if (searchStack.count > 1) top(searchStack.pop())!! else top

    val newTop = top.assoc(searchBar.query, trimmedQuery)
    val isDraftSb = top[results] == null
    val isSamePrevSearch = prevSb[searchBar.query] == trimmedQuery

    val newSbVec = when {
      isSamePrevSearch &&
        isDraftSb &&
        searchStack.count > 1 -> searchStack.pop().pop()

      else -> searchStack.pop()
    }
    val sbIndex = newSbVec.count
    val newDb = assocIn(
      appDb,
      l(activeTab, search_stack),
      newSbVec.conj(newTop.assoc(search_id, sbIndex))
    ).assoc(is_search_bar_active, false)

    val sq = trimmedQuery.replace(" ", "%20")
    m<Any, Any>(
      db to newDb,
      fx to v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to "${appDb[common.api_url]}/search?q=$sq&filter=all",
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[":search/coroutine_scope"],
            ktor.response_type_info to typeInfo<SearchResponse>(),
            ktor.on_success to v(common.set_search_results, sbIndex),
            ktor.on_failure to v(":error")
          )
        ),
        if (searchStack.count == 1) {
          v(navigate_to, m(common.destination to "$activeTab/$SEARCH_ROUTE"))
        } else null
      )
    )
  }

  regEventFx(id = common.search_back_press) { cofx, _ ->
    val appDb = appDbBy(cofx)
    val isSearchBarActive = appDb[is_search_bar_active] as Boolean
    val activeTab = getActiveTab(appDb)
    val searchStack = searchStack(appDb, activeTab)!!
    val sb = top(searchStack) as IPersistentMap<Any, Any>
    val searchDone = sb[search_id] != null

    if (isSearchBarActive && searchDone) {
      return@regEventFx m<Any, Any>(
        db to appDb.assoc(is_search_bar_active, false)
      )
    }

    val newSbVec = searchStack.pop()
    if (newSbVec.isEmpty()) {
      return@regEventFx m<Any, Any>(
        db to assocIn(appDb, l(activeTab), removeSearchBar(appDb, activeTab)),
        fx to v(v(common.pop_back_stack))
      )
    }

    m<Any, Any>(
      db to assocIn(appDb, l(activeTab, search_stack), newSbVec)
        .assoc(is_search_bar_active, false)
    )
  }

  regEventFx(id = common.clear_search_input) { cofx, _ ->
    m<Any, Any>(
      db to appDbBy(cofx).assoc(is_search_bar_active, true),
      fx to v(v(dispatch, v(common.search_input, "")))
    )
  }
}

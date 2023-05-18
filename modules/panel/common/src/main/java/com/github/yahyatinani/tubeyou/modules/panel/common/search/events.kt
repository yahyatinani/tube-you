package com.github.yahyatinani.tubeyou.modules.panel.common.search

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.BuiltInFx.dispatch
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.y.core.assoc
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.Associative
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.destination
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_search_bar_active
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.navigate_to
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.search_stack
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.search_suggestions
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.set_search_results
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.query
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.results
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.search_id
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.activeTab
import com.github.yahyatinani.tubeyou.modules.panel.common.appDbBy
import com.github.yahyatinani.tubeyou.modules.panel.common.bounce_fx
import com.github.yahyatinani.tubeyou.modules.panel.common.ktor
import com.github.yahyatinani.tubeyou.modules.panel.common.search.StackState.DRAFT
import com.github.yahyatinani.tubeyou.modules.panel.common.search.StackState.SAVED
import io.ktor.http.HttpMethod
import io.ktor.util.reflect.typeInfo

typealias SearchBar = Associative<Any, Any>
typealias SearchBarStack = PersistentVector<SearchBar>
typealias SearchStack = IPersistentMap<Any, Any>

private fun removeSearchBar(
  appDb: AppDb,
  activeTab: Any?
) = getIn<AppDb>(appDb, l(activeTab))!!.dissoc(search_stack)

private fun searchMap(appDb: AppDb, activeTab: Any?) =
  getIn<SearchStack>(appDb, l(activeTab, search_stack))

fun swapTop(
  searchBarStack: SearchBarStack,
  searchBar: SearchBar
): SearchBarStack = searchBarStack.pop().conj(searchBar)

fun top(searchBarStack: SearchBarStack?) = searchBarStack?.peek()

/*
 * SearchStack spec:
 * ============
 *
 * {:state (DRAFT, SAVED)
 *  :stack [search 1, search 2]
 * }
 */

enum class StackState { DRAFT, SAVED }

val defaultStack = m(
  "state" to DRAFT,
  "stack" to v(defaultSb)
)

fun hideSearchBar(db: AppDb): AppDb = db.assoc(is_search_bar_active, false)

fun newSearchStackDb(
  appDb: AppDb,
  activeTab: Any? = appDb[common.active_navigation_item],
  state: StackState,
  stack: SearchBarStack
) = assocIn(
  hideSearchBar(appDb),
  l(activeTab, search_stack),
  m<Any, Any>("stack" to stack, "state" to state)
)

fun regCommonEvents() {
  regEventFx(
    id = common.init_search_bar,
    interceptors = v(injectCofx(":search/coroutine_scope"))
  ) { cofx, _ ->
    val appDb = appDbBy(cofx).assoc(is_search_bar_active, true)
    val activeTab = activeTab(appDb)
    m<Any, Any>(
      db to assocIn(appDb, l(activeTab(appDb), search_stack), defaultStack),
      fx to v(v(navigate_to, m(destination to "$activeTab/$SEARCH_ROUTE")))
    )
  }

  regEventDb<AppDb>(id = is_search_bar_active) { db, (_, flag) ->
    db.assoc(is_search_bar_active, flag)
  }

  regEventDb<AppDb>(id = common.set_suggestions) { db, (_, suggestions) ->
    val activeTab = activeTab(db)
    val searchStack = searchStack(db, activeTab) ?: return@regEventDb db

    val newTop = top(searchStack)!!.assoc(searchBar.suggestions, suggestions)

    assocIn(
      db,
      l(activeTab, search_stack, "stack"),
      swapTop(searchStack, newTop)
    )
  }

  regEventFx(
    id = search_suggestions,
    interceptors = v(injectCofx(":search/coroutine_scope"))
  ) { cofx, (_, searchQuery) ->
    if ((searchQuery as String).isBlank() || searchQuery.isEmpty()) {
      return@regEventFx m<Any, Any>(
        fx to v(v(dispatch, v(common.set_suggestions, l<String>())))
      )
    }

    val sq = searchQuery.replace(" ", "%20")
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
    id = common.type_search,
    interceptors = v(injectCofx(":search/coroutine_scope"))
  ) { cofx, (_, searchQuery) ->
    val appDb = appDbBy(cofx)
    val activeTab = activeTab(appDb)
    val searchMap = searchMap(appDb, activeTab)!!
    val state: StackState = get<StackState>(searchMap, "state")!!
    val stack: SearchBarStack = get<SearchBarStack>(searchMap, "stack")!!

    val newAppDb = when (state) {
      DRAFT -> assocIn(
        appDb,
        l(activeTab, search_stack, "stack", stack.count - 1, query),
        searchQuery
      )

      SAVED -> newSearchStackDb(
        appDb = appDb,
        state = DRAFT,
        stack = stack.conj(defaultSb.assoc(query, searchQuery))
      )
    }

    m<Any, Any>(
      db to newAppDb,
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

  regEventDb<AppDb>(set_search_results) { db, (_, searchId, searchResults) ->
    val activeTab = activeTab(db)
    val searchStack = searchStack(db, activeTab)
    if (searchStack == null || searchStack.count <= searchId as Int) {
      return@regEventDb db
    }

    assocIn(
      db,
      l(activeTab, search_stack, "stack", searchId, results),
      (searchResults as SearchResponse).items
    )
  }

  regEventFx(
    id = common.search,
    interceptors = v(injectCofx(":search/coroutine_scope"))
  ) { cofx, (_, searchQuery) ->
    if ((searchQuery as String).isEmpty()) return@regEventFx m()

    val appDb = appDbBy(cofx)
    val activeTab = activeTab(appDb)
    val searchMap = searchMap(appDb, activeTab) ?: return@regEventFx m()

    val state = get<StackState>(searchMap, "state")!!
    val stack = get<SearchBarStack>(searchMap, "stack")!!
    val trimmedQuery = searchQuery.trim()
    val top = top(stack)!!

    val newStack = if (state == DRAFT) {
      if (top(stack.pop())?.get(query) == trimmedQuery) {
        stack.pop() // pop draft search.
      } else { // update the search query to trimmed one.
        swapTop(
          stack,
          assoc(top, query to trimmedQuery, search_id to stack.count - 1)
        )
      }
    } else stack

    val newAppDb = newSearchStackDb(appDb, activeTab, SAVED, newStack)
    val sq = trimmedQuery.replace(" ", "%20")
    val searchId = newStack.count - 1
    m<Any, Any>(
      db to newAppDb.assoc(is_search_bar_active, false),
      fx to v(
        v(
          ktor.http_fx,
          m(
            ktor.method to HttpMethod.Get,
            ktor.url to "${appDb[common.api_url]}/search?q=$sq&filter=all",
            ktor.timeout to 8000,
            ktor.coroutine_scope to cofx[":search/coroutine_scope"],
            ktor.response_type_info to typeInfo<SearchResponse>(),
            ktor.on_success to v(set_search_results, searchId),
            ktor.on_failure to v(":error")
          )
        )
      )
    )
  }

  regEventFx(id = common.search_back_press) { cofx, _ ->
    val appDb = appDbBy(cofx)
    val activeTab = activeTab(appDb)
    val searchMap = searchMap(appDb, activeTab)!!
    val state = get<StackState>(searchMap, "state")!!

    val isSearchBarActive = appDb[is_search_bar_active] as Boolean
    if (state == SAVED && isSearchBarActive) {
      return@regEventFx m<Any, Any>(db to hideSearchBar(appDb))
    }

    val newStack = searchStack(appDb, activeTab)!!.pop()
    if (newStack.isEmpty()) {
      return@regEventFx m<Any, Any>(
        db to assocIn(appDb, l(activeTab), removeSearchBar(appDb, activeTab)),
        fx to v(v(common.pop_back_stack))
      )
    }

    m<Any, Any>(db to newSearchStackDb(appDb, activeTab, SAVED, newStack))
  }

  regEventFx(id = common.clear_search_input) { cofx, _ ->
    m<Any, Any>(
      db to appDbBy(cofx).assoc(is_search_bar_active, true),
      fx to v(v(dispatch, v(common.type_search, "")))
    )
  }
}

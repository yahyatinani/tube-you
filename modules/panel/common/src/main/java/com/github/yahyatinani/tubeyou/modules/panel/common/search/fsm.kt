package com.github.yahyatinani.tubeyou.modules.panel.common.search

import androidx.paging.LoadState.Loading
import androidx.paging.LoadState.NotLoading
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.destination
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.navigate_to
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.activate_searchBar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.back_press_search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.clear_search_input
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.get_search_suggestions
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.search_list
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.set_search_results
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.set_suggestions
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.show_search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.update_search_input
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.activeTab
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.ACTIVE
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.INACTIVE
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchState.APPENDING
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchState.SEARCHING
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchState.SEARCH_RESULTS
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fsm.fsm.ALL
import io.github.yahyatinani.recompose.fsm.fsm.actions
import io.github.yahyatinani.recompose.fsm.fsm.guard
import io.github.yahyatinani.recompose.fsm.fsm.parallel
import io.github.yahyatinani.recompose.fsm.fsm.regions
import io.github.yahyatinani.recompose.fsm.fsm.target
import io.github.yahyatinani.recompose.fsm.fsm.type
import io.github.yahyatinani.recompose.fx.BuiltInFx.dispatch
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.recompose.httpfx.bounce
import io.github.yahyatinani.y.core.assocIn
import io.github.yahyatinani.y.core.collections.IPersistentMap
import io.github.yahyatinani.y.core.collections.IPersistentVector
import io.github.yahyatinani.y.core.collections.PersistentVector
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.getIn
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.merge
import io.github.yahyatinani.y.core.selectKeys
import io.github.yahyatinani.y.core.updateIn
import io.github.yahyatinani.y.core.v

/*
 * The state map spec:
 * ===================
 *
 * {:_state {:region1 s1 , region2 s2 }
 *  :search_bar {query "1", suggestions []}
 *  :stack [search_bar1, search_bar2]}
 */

typealias SearchBar = IPersistentMap<Any, Any?>
typealias SearchStack = PersistentVector<SearchBar>

// -- SearchPanel FSM ----------------------------------------------------------
enum class SearchState { SEARCHING, SEARCH_RESULTS, APPENDING }

fun isQueryBlankOrEmpty(appDb: AppDb, state: State?, event: Event): Boolean {
  val (_, searchQuery) = event
  return (searchQuery as String).isBlank() || searchQuery.isEmpty()
}

fun navigateToSearchPanel(appDb: AppDb, state: State?, event: Event): Effects {
  val activeTab = activeTab(appDb)
  return m(
    fx to v(v(navigate_to, m(destination to "$activeTab/$SEARCH_ROUTE")))
  )
}

fun isSearchStackEmpty(appDb: AppDb, state: State?, event: Event): Boolean {
  val searchStack = state?.get(search.stack) as SearchStack?
  return searchStack == null || searchStack.count == 0
}

fun updateSearchBarToMatchTopOfStack(
  appDb: AppDb,
  state: State?,
  event: Event
): Effects {
  val top = (state!![search.stack] as SearchStack).peek()
  return m(fsm.state_map to state.assoc(search_bar, top))
}

fun setResults(appDb: AppDb, state: State?, event: Event): Effects {
  val sb = event[1] as State
  val results = event[2]
  val searchStack = (state?.get(search.stack) as SearchStack? ?: v())
  val topSb: SearchBar? = searchStack.peek()

  val key = when (results) {
    is IPersistentVector<*> -> searchBar.results
    else -> searchBar.search_error
  }

  val newSearchStack = when {
    sb[searchBar.query] == topSb?.get(searchBar.query) -> { // dup
      val newSb: IPersistentMap<Any, Any?> = selectKeys(
        topSb,
        l(searchBar.query, searchBar.suggestions)
      ).assoc(key, results) as IPersistentMap<Any, Any?>

      searchStack.pop().conj(newSb)
    }

    else -> searchStack.conj(sb.assoc(key, results))
  }

  return m(fsm.state_map to state!!.assoc(search.stack, newSearchStack))
}

fun popSearchStack(appDb: AppDb, state: State?, event: Event): Effects {
  val searchStack = state?.get(search.stack) as SearchStack? ?: return m()
  return m(fsm.state_map to state!!.assoc(search.stack, searchStack.pop()))
}

fun isLastSearchBar(appDb: AppDb, state: State?, event: Event): Boolean {
  val st = state!![search.stack] as SearchStack? ?: return true
  return st.count <= 1
}

fun isSearchBarInactive(appDb: AppDb, state: State?, event: Event): Boolean =
  getIn<Any>(state, l(fsm._state, search_bar)) == INACTIVE

fun isSearching(appDb: AppDb, state: State?, event: Event): Boolean =
  getIn<Any>(state, l(fsm._state, search_list)) == SEARCHING

val searchListMachine = m<Any?, Any?>(
  null to m(
    search.submit to v(
      m(target to null, guard to ::isQueryBlankOrEmpty),
      m(target to SEARCHING, actions to ::navigateToSearchPanel)
    )
  ),
  SEARCHING to m(
    back_press_search to v(
      m(target to null, guard to ::isSearchStackEmpty),
      m(target to SEARCH_RESULTS, actions to ::updateSearchBarToMatchTopOfStack)
    ),
    set_search_results to m(target to SEARCH_RESULTS, actions to ::setResults),
    clear_search_input to v(
      m(target to null, guard to ::isSearchStackEmpty),
      m(target to SEARCH_RESULTS)
    )
  ),
  SEARCH_RESULTS to m(
    back_press_search to v(
      m(target to null, guard to v(::isLastSearchBar, ::isSearchBarInactive)),
      m(target to SEARCH_RESULTS)
    ),
    search.submit to v(
      m(target to SEARCH_RESULTS, guard to ::isQueryBlankOrEmpty),
      m(target to SEARCHING)
    ),
    Loading to m(target to APPENDING)
  ),
  APPENDING to m(
    set_search_results to m(target to SEARCH_RESULTS, actions to ::setResults),
    Loading to m(target to APPENDING),
    NotLoading(endOfPaginationReached = true) to m(target to SEARCH_RESULTS),
    back_press_search to v(
      m(target to null, guard to v(::isLastSearchBar, ::isSearchBarInactive)),
      m(target to SEARCH_RESULTS)
    )
  )
)

// -- SearchBar FSM ------------------------------------------------------------
enum class SearchBarState { ACTIVE, INACTIVE }

fun initSearchBar(appDb: AppDb, state: State?, event: Event): Effects =
  m(fsm.state_map to state!!.assoc(search_bar, defaultSb))

fun newSbInput(appDb: AppDb, state: State?, event: Event): Effects = m(
  fsm.state_map to assocIn(state, l(search_bar, searchBar.query), event[1])
)

fun getSearchSuggestions(appDb: AppDb, state: State?, event: Event): Effects {
  val (_, searchQuery) = event
  if ((searchQuery as String).isEmpty()) {
    return m(
      fsm.state_map to assocIn(
        state,
        l(search_bar, searchBar.suggestions),
        l<String>()
      )
    )
  }

  return m(
    fx to v(
      v(
        bounce.fx,
        m(
          bounce.id to get_search_suggestions,
          bounce.event to v(get_search_suggestions, b = searchQuery),
          bounce.delay to 500
        )
      )
    )
  )
}

fun setSuggestions(appDb: AppDb, state: State?, event: Event): Effects {
  val (_, sq, suggestions) = event
  val sb = state?.get(search_bar) as SearchBar
  if ((sb[searchBar.query] as String).trim() != (sq as String).trim()) {
    return m()
  }

  return m(
    fsm.state_map to state.assoc(
      search_bar,
      sb.assoc(searchBar.suggestions, suggestions)
    )
  )
}

fun backUpSearchBar(appDb: AppDb, state: State?, event: Event): Effects {
  val newState = updateIn(
    m = state,
    ks = l(search.stack),
    f = { stack: SearchStack ->
      stack.pop().conj(merge(stack.peek(), state!![search_bar])!!)
    }
  )
  return m(fsm.state_map to newState)
}

fun navigateBack(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v(common.pop_back_stack)))

fun trimSearchQuery(appDb: AppDb, state: State?, event: Event): Effects {
  val sb = state!![search_bar] as SearchBar
  return m(
    fsm.state_map to state.assoc(
      search_bar,
      sb.assoc(searchBar.query, (sb[searchBar.query] as String).trim())
    )
  )
}

fun submitSearchRequest(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v(dispatch, v(search.get_search_results, state))))

val searchBarMachine = m<Any?, Any?>(
  null to m(
    show_search_bar to m(target to ACTIVE, actions to ::initSearchBar),
    set_suggestions to m(target to null)
  ),
  ACTIVE to m(
    update_search_input to m(
      target to ACTIVE,
      actions to v(::newSbInput, ::getSearchSuggestions)
    ),
    back_press_search to v(
      m(target to null, guard to ::isSearchStackEmpty),
      m(target to INACTIVE, actions to ::updateSearchBarToMatchTopOfStack)
    ),
    search.submit to v(
      m(target to ACTIVE, guard to ::isQueryBlankOrEmpty),
      m(
        target to INACTIVE,
        actions to v(::trimSearchQuery, ::submitSearchRequest)
      )
    )
  ),
  INACTIVE to m(
    back_press_search to v(
      m(
        target to null,
        guard to ::isSearchStackEmpty,
        actions to ::navigateBack
      ),
      v(
        m(target to INACTIVE, guard to ::isSearching),
        v(
          m(
            target to null,
            guard to ::isLastSearchBar,
            actions to ::navigateBack
          ),
          m(
            target to INACTIVE,
            actions to v(::popSearchStack, ::updateSearchBarToMatchTopOfStack)
          )
        )
      )
    ),
    activate_searchBar to m(target to ACTIVE, actions to ::backUpSearchBar)
  ),
  ALL to m(
    set_suggestions to m(target to ALL, actions to ::setSuggestions),
    clear_search_input to m(target to ACTIVE, actions to ::initSearchBar)
  )
)

val searchPanelMachine = m<Any?, Any?>(
  type to parallel,
  regions to v(
    v(search_bar, searchBarMachine),
    v(search_list, searchListMachine)
  )
)

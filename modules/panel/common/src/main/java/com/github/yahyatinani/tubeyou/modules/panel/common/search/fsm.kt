package com.github.yahyatinani.tubeyou.modules.panel.common.search

import com.github.whyrising.recompose.events.Event
import com.github.whyrising.recompose.fx.BuiltInFx.dispatch
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.fx.Effects
import com.github.whyrising.recompose.ids.recompose.db
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
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.dispatch_debounce
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_search_bar_active
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.navigate_to
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.back_press_search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.clear_search_input
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.get_search_suggestions
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.set_search_results
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.set_suggestions
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.show_search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.stack
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.submit
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.update_search_input
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.State
import com.github.yahyatinani.tubeyou.modules.panel.common.activeTab
import com.github.yahyatinani.tubeyou.modules.panel.common.bounce_fx
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm.ALL
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm.actions
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm.guard
import com.github.yahyatinani.tubeyou.modules.panel.common.fsm.target
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.DRAFT
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.SEARCHING
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.SEARCH_RESULTS

// -- SearchBar FSM ------------------------------------------------------------

typealias SearchBar = Associative<Any, Any>
typealias SearchBarStack = PersistentVector<SearchBar>
typealias SearchBarFsm = Associative<Any, Any>

/*
 * The state map spec:
 * ============
 *
 * {:_state (DRAFT, SEARCHING, SEARCH_RESULTS)
 *  :stack [{ query "1", suggestions [])}, { query "2", suggestions [])}]}
 */

enum class SearchBarState { DRAFT, SEARCHING, SEARCH_RESULTS }

val initSearchBarFsm = m(fsm._state to DRAFT, stack to v(defaultSb))

fun searchBarFsm(appDb: AppDb, activeTab: Any? = activeTab(appDb)) =
  getIn<SearchBarFsm?>(appDb, l(activeTab, search.sb_state))

fun searchStack(appDb: AppDb, activeTab: Any? = activeTab(appDb)) =
  get<SearchBarStack>(searchBarFsm(appDb, activeTab), stack)

fun top(searchBarStack: SearchBarStack): SearchBar = searchBarStack.peek()!!

fun showSearchBar(appDb: AppDb): AppDb =
  appDb.assoc(is_search_bar_active, true)

fun searchBarStack(searchBarFsm: SearchBarFsm): SearchBarStack =
  searchBarFsm[stack] as SearchBarStack

fun currentState(sbFsm: SearchBarFsm?): SearchBarState? =
  sbFsm?.get(fsm._state) as SearchBarState?

fun topStackIndex(searchBarFsm: SearchBarFsm): Int =
  searchBarStack(searchBarFsm).count - 1

fun updateSbInput(searchBarFsm: SearchBarFsm, searchQuery: Any): SearchBarFsm =
  assocIn(
    searchBarFsm,
    l(stack, topStackIndex(searchBarFsm), searchBar.query),
    searchQuery
  )

fun updateState(sbFsm: SearchBarFsm, nextState: SearchBarState) =
  sbFsm.assoc(fsm._state, nextState)

fun duplicateSearchQuery(searchBarStack: SearchBarStack, topSb: SearchBar) =
  searchBarStack.count > 0 &&
    top(searchBarStack)[searchBar.query] == topSb[searchBar.query]

fun cleanUpSearchBar(sb: SearchBar) = (sb as IPersistentMap<Any, Any>)
  .dissoc(searchBar.results)
  .dissoc(searchBar.search_error)

fun searchSuggestions(sbFsm: SearchBarFsm?): List<String> {
  sbFsm ?: return l()
  return top(searchBarStack(sbFsm))[searchBar.suggestions] as List<String>
}

fun searchResults(sbFsm: SearchBarFsm) = get<PersistentVector<SearchResult>>(
  searchBarStack(sbFsm).peek()!!,
  searchBar.results
) ?: v()

fun searchError(sbFsm: SearchBarFsm): Int? = get<Int>(
  searchBarStack(sbFsm).peek()!!,
  searchBar.search_error
)

fun conjSearchBar(
  fsm: Associative<Any, Any>,
  searchBar: IPersistentMap<Any, Any>
) = fsm.assoc(stack, searchBarStack(fsm).conj(searchBar))

// -- FSM actions --------------------------------------------------------------

fun showSearchBar2(appDb: AppDb, state: State, event: Event): Effects =
  m(db to showSearchBar(appDb), fsm._state to initSearchBarFsm)

fun activateSearchBar(appDb: AppDb, state: State, event: Event): Effects =
  m(db to appDb.assoc(is_search_bar_active, true))

fun navigateEffect(appDb: AppDb, state: State, event: Event): Effects {
  val activeTab = activeTab(appDb)
  return m(
    fx to v(v(navigate_to, m(destination to "$activeTab/$SEARCH_ROUTE")))
  )
}

fun updateSbInput2(appDb: AppDb, state: State, event: Event): Effects =
  m(fsm._state to updateSbInput(state as SearchBarFsm, event[1]))

fun searchSuggestionsFx(appDb: AppDb, state: State, event: Event): Effects {
  val (_, query) = event
  if ((query as String).isBlank() || query.isEmpty()) {
    val searchBarStack = searchBarStack(state as SearchBarFsm)
    return m(
      fsm._state to assocIn(
        state,
        l<Any>(stack, searchBarStack.count - 1, searchBar.suggestions),
        l<String>()
      )
    )
  }

  return m(
    fx to v(
      v(
        dispatch_debounce,
        m(
          bounce_fx.id to get_search_suggestions,
          bounce_fx.event to v(get_search_suggestions, b = query),
          bounce_fx.delay to 500
        )
      )
    )
  )
}

fun clearSbInput(appDb: AppDb, state: State, event: Event): Effects =
  m(fsm._state to updateSbInput(state as SearchBarFsm, searchQuery = ""))

fun conjNewSearchBar(appDb: AppDb, state: State, event: Event): Effects =
  m(fsm._state to conjSearchBar(state as SearchBarFsm, defaultSb))

fun conjSearchBarQuery(appDb: AppDb, state: State, event: Event): Effects {
  val (_, sq) = event
  val newSb = defaultSb.assoc(searchBar.query, sq)
  return m(fsm._state to conjSearchBar(state as SearchBarFsm, newSb))
}

fun isQueryBlankOrEmpty(appDb: AppDb, state: State, event: Event): Boolean {
  val (_, searchQuery) = event
  return (searchQuery as String).isBlank() || searchQuery.isEmpty()
}

fun trimUpdateInput(appDb: AppDb, state: State, event: Event): Effects {
  val (_, query) = event
  val searchQuery = (query as String).trim()
  return m(fsm._state to updateSbInput(state as SearchBarFsm, searchQuery))
}

fun deactivateSearchBar(appDb: AppDb, state: State, event: Event): Effects =
  m(db to appDb.assoc(is_search_bar_active, false))

fun getSearchResults(appDb: AppDb, state: State, event: Event): Effects {
  val topStackIndex = topStackIndex(state as SearchBarFsm)
  val query = getIn<String>(state, l(stack, topStackIndex, searchBar.query))
  return m(fx to v(v(dispatch, v(search.get_search_results, query))))
}

fun setResults(appDb: AppDb, state: State, event: Event): Effects {
  val searchBarStack = searchBarStack(state!! as SearchBarFsm)
  val top = cleanUpSearchBar(top(searchBarStack)).run {
    val (_, results) = event
    if (results is SearchResponse) assoc(searchBar.results, results.items)
    else assoc(searchBar.search_error, results)
  }

  val newStack = searchBarStack.pop().let {
    if (duplicateSearchQuery(it, top)) it.pop().conj(top) else it.conj(top)
  }
  return m(fsm._state to state.assoc(stack, newStack))
}

fun isLastSearchBar(appDb: AppDb, state: State, event: Event): Boolean =
  searchBarStack(state!! as SearchBarFsm).count <= 1

fun popBackStack(appDb: AppDb, state: State, event: Event): Effects =
  m(fx to v(v(common.pop_back_stack)))

fun popSearchBar(appDb: AppDb, state: State, event: Event): Effects {
  val searchBarStack = searchBarStack(state!! as SearchBarFsm)
  return m(fsm._state to state.assoc(stack, searchBarStack.pop()))
}

fun isSearchBarActive(appDb: AppDb, state: State, event: Event): Boolean =
  appDb[is_search_bar_active] as Boolean

fun setSuggestions(appDb: AppDb, state: State, event: Event): Effects {
  val (_, searchQuery, suggestions) = event
  val searchBarStack = searchBarStack(state as SearchBarFsm)

  if (top(searchBarStack)[searchBar.query] != searchQuery) return m()

  return m(
    fsm._state to assocIn(
      state,
      l<Any>(stack, searchBarStack.count - 1, searchBar.suggestions),
      suggestions
    )
  )
}

val searchMachine = m<Any?, Any?>(
  null to m(
    show_search_bar to m(
      target to DRAFT,
      actions to v(::showSearchBar2, ::navigateEffect)
    ),
    set_suggestions to m(target to null),
    set_search_results to m(target to null)
  ),
  DRAFT to m(
    update_search_input to m(
      target to DRAFT,
      actions to v(::updateSbInput2, ::searchSuggestionsFx)
    ),
    clear_search_input to m(target to DRAFT, actions to ::clearSbInput),
    submit to v(
      m(target to DRAFT, guard to ::isQueryBlankOrEmpty),
      m(
        target to SEARCHING,
        actions to v(
          ::trimUpdateInput,
          ::deactivateSearchBar,
          ::getSearchResults
        )
      )
    ),
    back_press_search to v(
      m(target to null, guard to ::isLastSearchBar, actions to ::popBackStack),
      m(
        target to SEARCH_RESULTS,
        actions to v(::deactivateSearchBar, ::popSearchBar)
      )
    )
  ),
  SEARCHING to m(
    clear_search_input to m(
      target to DRAFT,
      actions to v(::showSearchBar2, ::clearSbInput)
    ),
    back_press_search to v(
      m(
        target to null,
        guard to ::isLastSearchBar,
        actions to ::popBackStack
      ),
      m(
        target to SEARCH_RESULTS,
        actions to v(::deactivateSearchBar, ::popSearchBar)
      )
    ),
    set_search_results to m(target to SEARCH_RESULTS, actions to ::setResults)
  ),
  SEARCH_RESULTS to m(
    back_press_search to v(
      m(
        target to SEARCH_RESULTS,
        guard to ::isSearchBarActive,
        actions to ::deactivateSearchBar
      ),
      v(
        m(
          target to null,
          guard to ::isLastSearchBar,
          actions to ::popBackStack
        ),
        m(target to SEARCH_RESULTS, actions to ::popSearchBar)
      )
    ),
    update_search_input to m(
      target to DRAFT,
      actions to v(::conjSearchBarQuery, ::searchSuggestionsFx)
    ),
    clear_search_input to m(
      target to DRAFT,
      actions to v(::activateSearchBar, ::conjNewSearchBar)
    ),
    submit to m(
      target to SEARCHING,
      actions to v(::deactivateSearchBar, ::getSearchResults)
    )
  ),
  ALL to m(set_suggestions to m(target to ALL, actions to ::setSuggestions))
)

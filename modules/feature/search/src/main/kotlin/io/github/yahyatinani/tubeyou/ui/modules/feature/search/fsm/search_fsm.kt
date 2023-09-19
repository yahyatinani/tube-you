package io.github.yahyatinani.tubeyou.ui.modules.feature.search.fsm

import androidx.paging.ItemSnapshotList
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.APPENDING
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.LOADED
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.LOADING
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import io.github.yahyatinani.recompose.events.Event
import io.github.yahyatinani.recompose.fsm.AppDb
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.fsm.fsm.actions
import io.github.yahyatinani.recompose.fsm.fsm.guard
import io.github.yahyatinani.recompose.fsm.fsm.target
import io.github.yahyatinani.recompose.fx.BuiltInFx
import io.github.yahyatinani.recompose.fx.BuiltInFx.fx
import io.github.yahyatinani.recompose.fx.Effects
import io.github.yahyatinani.recompose.httpfx.bounce
import io.github.yahyatinani.tubeyou.common.activeTopLevelRoute
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.db.SearchBar
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.db.SearchStack
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.db.defaultSb
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.fsm.SearchBarState.ACTIVE
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.fsm.SearchBarState.INACTIVE
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.navigation.SEARCH_ROUTE
import io.github.yahyatinani.y.core.assocIn
import io.github.yahyatinani.y.core.collections.IPersistentMap
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

// -- SearchPanel FSM ----------------------------------------------------------
fun isQueryBlankOrEmpty(appDb: AppDb, state: State?, event: Event): Boolean {
  val (_, searchQuery) = event
  return (searchQuery as String).isBlank() || searchQuery.isEmpty()
}

fun navigateToSearchPanel(appDb: AppDb, state: State?, event: Event): Effects {
  val activeTab = activeTopLevelRoute(appDb)
  return m(
    fx to v(
      v(
        common.navigate_to,
        m(common.destination to "$activeTab/$SEARCH_ROUTE")
      )
    )
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
  return m(fsm.state_map to state.assoc(search.search_bar, top))
}

fun setResults(appDb: AppDb, state: State?, event: Event): Effects {
  val results = event[1]
  val sb = event[2] as State
  val searchStack = (state?.get(search.stack) as SearchStack? ?: v())
  val topSb: SearchBar? = searchStack.peek()

  val key = when (results) {
    is ItemSnapshotList<*> -> searchBar.results
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
  getIn<Any>(state, l(fsm._state, search.search_bar)) == INACTIVE

fun isSearching(appDb: AppDb, state: State?, event: Event): Boolean =
  getIn<Any>(state, l(fsm._state, search.search_list)) == LOADING

val searchListMachine = m<Any?, Any?>(
  null to m(
    search.submit to v(
      m(target to null, guard to ::isQueryBlankOrEmpty),
      m(target to LOADING, actions to ::navigateToSearchPanel)
    )
  ),
  LOADING to m(
    search.back_press_search to v(
      m(target to null, guard to ::isSearchStackEmpty),
      m(target to LOADED)
    ),
    v("append_search_results", "done_loading") to m(
      target to LOADED,
      actions to ::setResults
    ),
    v("append_search_results", "error") to m(
      target to LOADED,
      actions to ::setResults
    ),
    search.clear_search_input to v(
      m(target to null, guard to ::isSearchStackEmpty),
      m(target to LOADED)
    )
  ),
  LOADED to m(
    search.back_press_search to v(
      m(
        target to null,
        guard to v(::isLastSearchBar, ::isSearchBarInactive)
      ),
      m(target to LOADED)
    ),
    search.submit to v(
      m(target to LOADED, guard to ::isQueryBlankOrEmpty),
      m(target to LOADING)
    ),
    v("append_search_results", "loading") to m(target to APPENDING)
  ),
  APPENDING to m(
    v("append_search_results", "loading") to m(target to APPENDING),
    v("append_search_results", "done_loading") to m(
      target to LOADED,
      actions to ::setResults
    ),
    search.back_press_search to v(
      m(
        target to null,
        guard to v(::isLastSearchBar, ::isSearchBarInactive)
      ),
      m(target to LOADED)
    )
  )
)

// -- SearchBar FSM ------------------------------------------------------------
enum class SearchBarState { ACTIVE, INACTIVE }

fun initSearchBar(appDb: AppDb, state: State?, event: Event): Effects =
  m(fsm.state_map to state!!.assoc(search.search_bar, defaultSb))

fun newSbInput(appDb: AppDb, state: State?, event: Event): Effects = m(
  fsm.state_map to assocIn(
    state,
    l(search.search_bar, searchBar.query),
    event[1]
  )
)

fun getSearchSuggestions(appDb: AppDb, state: State?, event: Event): Effects {
  val (_, searchQuery) = event
  if ((searchQuery as String).isEmpty()) {
    return m(
      fsm.state_map to assocIn(
        state,
        l(search.search_bar, searchBar.suggestions),
        l<String>()
      )
    )
  }

  return m(
    fx to v(
      v(
        bounce.fx,
        m(
          bounce.id to search.get_search_suggestions,
          bounce.event to v(search.get_search_suggestions, searchQuery),
          bounce.delay to 500
        )
      )
    )
  )
}

fun setSuggestions(appDb: AppDb, state: State?, event: Event): Effects {
  val (_, sq, suggestions) = event
  val sb = state?.get(search.search_bar) as SearchBar
  if ((sb[searchBar.query] as String).trim() != (sq as String).trim()) {
    return m()
  }

  return m(
    fsm.state_map to state.assoc(
      search.search_bar,
      sb.assoc(searchBar.suggestions, suggestions)
    )
  )
}

fun backUpSearchBar(appDb: AppDb, state: State?, event: Event): Effects {
  val newState = updateIn(
    m = state,
    ks = l(search.stack),
    f = { stack: SearchStack ->
      stack.pop().conj(merge(stack.peek(), state!![search.search_bar])!!)
    }
  )
  return m(fsm.state_map to newState)
}

fun navigateBack(appDb: AppDb, state: State?, event: Event): Effects =
  m(fx to v(v(common.pop_back_stack)))

fun trimSearchQuery(appDb: AppDb, state: State?, event: Event): Effects {
  val sb = state!![search.search_bar] as SearchBar
  return m(
    fsm.state_map to state.assoc(
      search.search_bar,
      sb.assoc(searchBar.query, (sb[searchBar.query] as String).trim())
    )
  )
}

fun submitSearchRequest(appDb: AppDb, state: State?, event: Event): Effects =
  m(
    fx to v(
      v(
        BuiltInFx.dispatch,
        v(search.get_search_results, state)
      )
    )
  )

val searchBarMachine = m<Any?, Any?>(
  null to m(
    search.show_search_bar to m(
      target to ACTIVE,
      actions to ::initSearchBar
    ),
    search.set_suggestions to m(target to null)
  ),
  ACTIVE to m(
    search.update_search_input to m(
      target to ACTIVE,
      actions to v(::newSbInput, ::getSearchSuggestions)
    ),
    search.back_press_search to v(
      m(target to null, guard to ::isSearchStackEmpty),
      m(
        target to INACTIVE,
        actions to ::updateSearchBarToMatchTopOfStack
      )
    ),
    v(search.activate_searchBar, false) to v(
      m(target to null, guard to ::isSearchStackEmpty),
      m(
        target to INACTIVE,
        actions to ::updateSearchBarToMatchTopOfStack
      )
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
    search.back_press_search to v(
      m(
        target to null,
        guard to ::isSearchStackEmpty,
        actions to ::navigateBack
      ),
      v(
        m(
          target to INACTIVE,
          guard to ::isSearching,
          actions to ::updateSearchBarToMatchTopOfStack
        ),
        v(
          m(
            target to null,
            guard to ::isLastSearchBar,
            actions to ::navigateBack
          ),
          m(
            target to INACTIVE,
            actions to v(
              ::popSearchStack,
              ::updateSearchBarToMatchTopOfStack
            )
          )
        )
      )
    ),
    v(search.activate_searchBar, true) to m(
      target to ACTIVE,
      actions to ::backUpSearchBar
    )
  ),
  fsm.ALL to m(
    search.set_suggestions to m(
      target to fsm.ALL,
      actions to ::setSuggestions
    ),
    search.clear_search_input to m(
      target to ACTIVE,
      actions to ::initSearchBar
    )
  )
)

val searchPanelMachine = m<Any?, Any?>(
  fsm.type to fsm.parallel,
  fsm.regions to v(
    v(search.search_bar, searchBarMachine),
    v(search.search_list, searchListMachine)
  )
)

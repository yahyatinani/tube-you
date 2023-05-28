package com.github.yahyatinani.tubeyou.modules.panel.common.search

import android.util.Log
import com.github.whyrising.recompose.fx.BuiltInFx
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
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.stack
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.update_search_input
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.panel.common.AppDb
import com.github.yahyatinani.tubeyou.modules.panel.common.activeTab
import com.github.yahyatinani.tubeyou.modules.panel.common.bounce_fx
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.DRAFT
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.SEARCHING
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.SEARCH_RESULTS

// -- SearchBar FSM ------------------------------------------------------------

typealias SearchBar = Associative<Any, Any>
typealias SearchBarFsm = Associative<Any, Any>
typealias SearchBarStack = PersistentVector<SearchBar>

/*
 * SearchBarFsm spec:
 * ============
 *
 * {:state (DRAFT, SEARCHING, SEARCH_DONE)
 *  :stack [{ query "1", suggestions [])}, { query "2", suggestions [])}]}
 */

enum class SearchBarState { DRAFT, SEARCHING, SEARCH_RESULTS }

val initSearchBarFsm = m(search.state to DRAFT, stack to v(defaultSb))

fun searchBarFsm(appDb: AppDb, activeTab: Any? = activeTab(appDb)) =
  getIn<SearchBarFsm?>(appDb, l(activeTab, search.sb_fsm))

fun searchStack(appDb: AppDb, activeTab: Any? = activeTab(appDb)) =
  get<SearchBarStack>(searchBarFsm(appDb, activeTab), stack)

fun top(searchBarStack: SearchBarStack): SearchBar = searchBarStack.peek()!!

fun showSearchBar(appDb: AppDb): AppDb =
  appDb.assoc(is_search_bar_active, true)

fun setSbFsm(
  appDb: AppDb,
  activeTab: Any,
  searchBarFsm: Associative<Any, Any> = initSearchBarFsm
) = assocIn(appDb, l(activeTab, search.sb_fsm), searchBarFsm)

fun searchBarStack(searchBarFsm: SearchBarFsm): SearchBarStack =
  searchBarFsm[stack] as SearchBarStack

fun currentState(sbFsm: SearchBarFsm?): SearchBarState? =
  sbFsm?.get(search.state) as SearchBarState?

fun topStackIndex(searchBarFsm: SearchBarFsm): Int =
  searchBarStack(searchBarFsm).count - 1

fun updateSbInput(searchBarFsm: SearchBarFsm, searchQuery: Any): SearchBarFsm {
  val topStackIndex = topStackIndex(searchBarFsm)
  return assocIn(
    searchBarFsm,
    l(stack, topStackIndex, searchBar.query),
    searchQuery
  )
}

/**
 * todo: updateIn.
 */
fun removeSearchBarFsm(appDb: AppDb, activeTab: Any?) =
  assocIn(
    appDb,
    l(activeTab),
    getIn<AppDb>(appDb, l(activeTab))!!.dissoc(search.sb_fsm)
  )

fun collapseSearchBar(db: AppDb): AppDb =
  db.assoc(is_search_bar_active, false)

fun updateState(sbFsm: SearchBarFsm, nextState: SearchBarState) =
  sbFsm.assoc(search.state, nextState)

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

fun searchSuggestionsFx(searchQuery: Any) = v(
  dispatch_debounce,
  m(
    bounce_fx.id to get_search_suggestions,
    bounce_fx.event to v(get_search_suggestions, searchQuery),
    bounce_fx.delay to 500
  )
)

fun conjSearchBar(
  fsm: Associative<Any, Any>,
  searchBar: IPersistentMap<Any, Any>
) = fsm.assoc(stack, searchBarStack(fsm).conj(searchBar))

fun popTopSearchBar(appDb: AppDb, sbFsm: SearchBarFsm?, activeTab: Any): AppDb {
  val searchBarStack = searchBarStack(sbFsm!!)
  if (searchBarStack.count > 1) {
    return m(
      db to setSbFsm(
        collapseSearchBar(appDb),
        activeTab,
        sbFsm.assoc(stack, searchBarStack.pop())
      )
    )
  }

  return m(
    db to removeSearchBarFsm(appDb, activeTab),
    fx to v(v(common.pop_back_stack))
  )
}

/* FSM transitions: */
val NULL_show_search_bar = v(null, search.show_search_bar)
val DRAFT__update_search_input = v(DRAFT, update_search_input)
val DRAFT__searchBackPress = v(DRAFT, back_press_search)
val DRAFT__searchSubmit = v(DRAFT, search.submit)
val SEARCH_DONE__searchSubmit = v(SEARCH_RESULTS, search.submit)

/** The search bar FSM implementation. */
fun sbFsm(appDb: AppDb, event: Any, vararg args: Any): Effects {
  val activeTab = activeTab(appDb)
  val sbFsm = searchBarFsm(appDb, activeTab)
  val currentState = currentState(sbFsm)

  return when (v(currentState, event)) {
    NULL_show_search_bar -> m(
      db to setSbFsm(showSearchBar(appDb), activeTab, initSearchBarFsm),
      fx to v(v(navigate_to, m(destination to "$activeTab/$SEARCH_ROUTE")))
    )

    DRAFT__update_search_input,
    v(DRAFT, clear_search_input),
    v(SEARCHING, clear_search_input) -> m(
      db to setSbFsm(
        appDb = showSearchBar(appDb),
        activeTab = activeTab,
        searchBarFsm = updateSbInput(updateState(sbFsm!!, DRAFT), args[0])
      ),
      fx to v(searchSuggestionsFx(searchQuery = args[0]))
    )

    DRAFT__searchSubmit, SEARCH_DONE__searchSubmit -> {
      val searchQuery = "${args[0]}".trim()
      m(
        db to setSbFsm(
          appDb = collapseSearchBar(appDb),
          activeTab = activeTab,
          searchBarFsm = updateSbInput(
            searchBarFsm = updateState(sbFsm!!, SEARCHING),
            searchQuery = searchQuery
          )
        ),
        fx to v(
          v(
            BuiltInFx.dispatch,
            v(search.get_search_results, searchQuery)
          )
        )
      )
    }

    v(SEARCH_RESULTS, back_press_search) -> {
      if (get<Boolean>(appDb, is_search_bar_active)!!) {
        return m(db to collapseSearchBar(appDb))
      }

      popTopSearchBar(appDb, sbFsm, activeTab)
    }

    v(SEARCHING, back_press_search), DRAFT__searchBackPress -> {
      popTopSearchBar(appDb, updateState(sbFsm!!, SEARCH_RESULTS), activeTab)
    }

    v(SEARCHING, search.set_search_results) -> {
      val searchBarFsm = updateState(sbFsm!!, SEARCH_RESULTS)
      val searchBarStack = searchBarStack(searchBarFsm)
      val top = cleanUpSearchBar(top(searchBarStack))
        .run {
          val (results) = args
          if (results is SearchResponse) assoc(searchBar.results, results.items)
          else assoc(searchBar.search_error, results)
        }

      val newStack = searchBarStack.pop().let {
        if (duplicateSearchQuery(it, top)) it.pop().conj(top) else it.conj(top)
      }

      m(
        db to setSbFsm(
          appDb = appDb,
          activeTab = activeTab,
          searchBarFsm = searchBarFsm.assoc(stack, newStack)
        )
      )
    }

    v(SEARCH_RESULTS, update_search_input),
    v(SEARCH_RESULTS, clear_search_input) -> m(
      db to setSbFsm(
        appDb = showSearchBar(appDb),
        activeTab = activeTab,
        searchBarFsm = conjSearchBar(
          fsm = updateState(sbFsm!!, DRAFT),
          searchBar = defaultSb.assoc(searchBar.query, args[0])
        )
      ),
      fx to v(searchSuggestionsFx(searchQuery = args[0]))
    )

    else -> {
      Log.w(
        "FSM",
        "State transition not found. : ${currentState(sbFsm)}, $event"
      )
      m()
    }
  }
}

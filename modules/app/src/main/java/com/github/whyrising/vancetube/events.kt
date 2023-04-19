package com.github.whyrising.vancetube

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.BuiltInFx.dispatch
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.LIBRARY_GRAPH_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.active_navigation_item
import com.github.whyrising.vancetube.modules.core.keywords.common.is_backstack_empty
import com.github.whyrising.vancetube.modules.core.keywords.common.is_online
import com.github.whyrising.vancetube.modules.core.keywords.common.is_search_bar_active
import com.github.whyrising.vancetube.modules.core.keywords.common.navigate_to
import com.github.whyrising.vancetube.modules.core.keywords.common.search_bar
import com.github.whyrising.vancetube.modules.core.keywords.common.search_suggestions
import com.github.whyrising.vancetube.modules.core.keywords.common.set_backstack_status
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.searchBar
import com.github.whyrising.vancetube.modules.core.keywords.searchBar.results
import com.github.whyrising.vancetube.modules.core.keywords.searchBar.search_id
import com.github.whyrising.vancetube.modules.panel.common.AppDb
import com.github.whyrising.vancetube.modules.panel.common.Suggestions
import com.github.whyrising.vancetube.modules.panel.common.appDbBy
import com.github.whyrising.vancetube.modules.panel.common.bounce_fx
import com.github.whyrising.vancetube.modules.panel.common.defaultSb
import com.github.whyrising.vancetube.modules.panel.common.letIf
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

typealias AppDb = IPersistentMap<Any, Any>

private fun removeSearchBar(
  appDb: AppDb,
  activeTab: Any?
) = getIn<IPersistentMap<Any, Any>>(appDb, l(activeTab))!!.dissoc(search_bar)

fun regAppEvents() {
  regEventFx(
    id = common.initialize,
    interceptors = v(injectCofx(is_online))
  ) { cofx, _ ->
    val isOnline = cofx[is_online]!! as Boolean
    val startingRoute = if (isOnline) HOME_GRAPH_ROUTE else LIBRARY_GRAPH_ROUTE
    m<Any, Any>(
      db to defaultDb.assoc(active_navigation_item, startingRoute)
    )
  }

  regEventDb<AppDb>(set_backstack_status) { db, (_, flag) ->
    db.assoc(common.is_backstack_available, flag)
  }

  // TODO: rethink this event handler
  regEventFx(
    id = active_navigation_item,
    interceptors = v(injectCofx(is_backstack_empty))
  ) { cofx, (_, destination) ->
    m(
      db to appDbBy(cofx)
        .assoc(is_backstack_empty, cofx[is_backstack_empty] as Boolean)
        .letIf(navItems[destination] != null) {
          it.assoc(active_navigation_item, destination)
        }
    )
  }

  regEventFx(id = common.on_click_nav_item) { cofx, (_, destination) ->
    // TODO: Set active_panel to active_navigation_item
    val appDb = appDbBy(cofx)
    m<Any, Any>(
      db to appDb.assoc(active_navigation_item, destination),
      fx to v(
        if (destination == appDb[active_navigation_item]) {
          // TODO: Use one fx for all panels to scroll up by overriding reg fx
          v(home.go_top_list)
        } else v(navigate_to, destination)
      )
    )
  }

  regEventDb<AppDb>(id = is_search_bar_active) { db, (_, flag) ->
    db.assoc(is_search_bar_active, flag)
  }

  regEventDb<AppDb>(id = common.show_search_bar) { db, _ ->
    assocIn(db, l(db[active_navigation_item], search_bar), v(defaultSb))
      .assoc(is_search_bar_active, true)
  }

  regEventFx(
    id = common.search_input,
    interceptors = v(injectCofx(home.coroutine_scope))
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

    val sb = sbVec
      .last()
      .assoc(searchBar.suggestions, (suggestions as Suggestions).value)

    assocIn(db, l(activeTab, search_bar), sbVec.pop().conj(sb))
  }

  regEventDb<AppDb>(
    id = common.set_search_results
  ) { db, (_, searchId, searchResults) ->
    val activeTab = db[active_navigation_item]
    val vec = getIn<PersistentVector<Any>>(db, l(activeTab, search_bar))

    if (vec == null || vec.count <= searchId as Int) return@regEventDb db

    assocIn(db, l(activeTab, search_bar, searchId, results), searchResults)
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

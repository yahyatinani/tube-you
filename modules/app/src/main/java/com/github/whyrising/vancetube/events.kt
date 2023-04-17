package com.github.whyrising.vancetube

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.fx.BuiltInFx.dispatch
import com.github.whyrising.recompose.fx.BuiltInFx.fx
import com.github.whyrising.recompose.ids.recompose.db
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.recompose.regEventFx
import com.github.whyrising.vancetube.modules.core.keywords.HOME_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.LIBRARY_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.SUBSCRIPTION_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.active_navigation_item
import com.github.whyrising.vancetube.modules.core.keywords.common.is_backstack_empty
import com.github.whyrising.vancetube.modules.core.keywords.common.is_online
import com.github.whyrising.vancetube.modules.core.keywords.common.is_search_bar_active
import com.github.whyrising.vancetube.modules.core.keywords.common.navigate_to
import com.github.whyrising.vancetube.modules.core.keywords.common.pop_back_stack
import com.github.whyrising.vancetube.modules.core.keywords.common.search_bar
import com.github.whyrising.vancetube.modules.core.keywords.common.search_bar_bak
import com.github.whyrising.vancetube.modules.core.keywords.common.set_backstack_status
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.searchBar
import com.github.whyrising.vancetube.modules.panel.common.AppDb
import com.github.whyrising.vancetube.modules.panel.common.appDbBy
import com.github.whyrising.vancetube.modules.panel.common.bounce_fx
import com.github.whyrising.vancetube.modules.panel.common.letIf
import com.github.whyrising.y.core.assocIn
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentList
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

typealias AppDb = IPersistentMap<Any, Any>

private fun removeSearchBar(
  appDb: AppDb,
  activeTab: Any?
) = getIn<IPersistentMap<Any?, *>>(appDb, l(activeTab))!!.dissoc(search_bar)

private fun popLastSearch(searchResultsSeq: PersistentList<*>?) =
  searchResultsSeq?.rest() ?: l()

private fun isSearchDone(searchResultsSeq: PersistentList<*>?) =
  searchResultsSeq != null

fun regAppEvents() {
  regEventFx(
    id = common.initialize,
    interceptors = v(injectCofx(is_online))
  ) { cofx, _ ->
    val isOnline = cofx[is_online]!! as Boolean
    val startingRoute = if (isOnline) HOME_ROUTE else LIBRARY_ROUTE
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

  regEventFx(navigate_to) { _, (_, destination) ->
    m<Any, Any>(fx to v(v(navigate_to, destination)))
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

  val defaultSb = m(searchBar.query to "", searchBar.suggestions to v<String>())

  regEventDb<AppDb>(id = common.show_search_bar) { db, _ ->
    val activeTab = db[active_navigation_item]
    val sb = getIn<Any>(db, l(activeTab, search_bar), defaultSb)!!

    assocIn(db, l(activeTab, search_bar), sb)
      .assoc(is_search_bar_active, true)
  }

  regEventDb<AppDb>(id = ":hide_search_bar") { db, _ ->
    db.assoc(
      HOME_ROUTE,
      (db[HOME_ROUTE] as IPersistentMap<Any?, *>).dissoc(search_bar)
    )
    when (db[active_navigation_item]) {
      HOME_ROUTE -> {
        // TODO: use dissoc
        db.assoc(
          HOME_ROUTE,
          (db[HOME_ROUTE] as IPersistentMap<Any?, *>).dissoc(search_bar)
        )
      }

      SUBSCRIPTION_ROUTE -> {
        // TODO: use dissoc
        db.assoc(
          SUBSCRIPTION_ROUTE,
          (db[SUBSCRIPTION_ROUTE] as IPersistentMap<Any?, *>).dissoc(search_bar)
        )
      }

      LIBRARY_ROUTE -> {
        // TODO: use dissoc
        db.assoc(
          LIBRARY_ROUTE,
          (db[LIBRARY_ROUTE] as IPersistentMap<Any?, *>).dissoc(search_bar)
        )
      }

      else -> TODO()
    }
  }

  regEventFx(
    id = searchBar.query,
    interceptors = v(injectCofx(home.coroutine_scope))
  ) { cofx, (_, searchQuery) ->
    val appDb = appDbBy(cofx)
    val activeTab = appDb[active_navigation_item]
    val newDb =
      assocIn(appDb, l(activeTab, search_bar, searchBar.query), searchQuery)

    m<Any, Any>(
      db to newDb,
      fx to v(
        v(
          common.dispatch_debounce,
          m(
            bounce_fx.id to ":search_suggestions",
            bounce_fx.event to v(":search_suggestions", searchQuery),
            bounce_fx.delay to 500
          )
        )
      )
    )
  }

  regEventFx(common.back_press) { _, _ ->
    m<Any, Any>(fx to v(v(common.back_press)))
  }

  regEventFx(id = common.search_back_press) { cofx, _ ->
    val appDb = appDbBy(cofx)
    val isSearchBarActive = appDb[is_search_bar_active] as Boolean
    val activeTab = appDb[active_navigation_item]
    val searchResultsSeq = getIn<PersistentList<*>>(
      appDb,
      l(activeTab, search_bar, searchBar.results)
    )

    if (isSearchBarActive) {
      if (searchResultsSeq == null) { // when :clear_search_text pressed.
        val sbBak = appDb[search_bar_bak]!!
        return@regEventFx m(
          db to assocIn(appDb, l(activeTab, search_bar), sbBak)
            .assoc(is_search_bar_active, false)
        )
      }
    }

    val rest = popLastSearch(searchResultsSeq)
    val newDb = if (rest.count > 0) {
      assocIn(appDb, l(activeTab, search_bar, searchBar.results), rest)
    } else {
      assocIn(appDb, l(activeTab), removeSearchBar(appDb, activeTab))
    }

    m<Any, Any>(
      db to newDb,
      fx to v(
        if (isSearchDone(searchResultsSeq)) v(pop_back_stack) else null
      )
    )
  }

  regEventFx(id = common.clear_search_text) { cofx, _ ->
    val appDb = appDbBy(cofx)
    val activeTab = appDb[active_navigation_item]
    val searchBarBackup = getIn<Any>(appDb, l(activeTab, search_bar))!!
    m<Any, Any>(
      db to assocIn(appDb, l(activeTab, search_bar), defaultSb)
        .assoc(search_bar_bak, searchBarBackup)
        .assoc(is_search_bar_active, true),
      fx to v(
        v(dispatch, v(searchBar.query, "")),
        v(dispatch, v(common.show_search_bar))
      )
    )
  }
}

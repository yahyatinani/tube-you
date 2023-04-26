package com.github.yahyatinani.tubeyou

import android.content.res.Resources
import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.y.core.assoc
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.collections.PersistentArrayMap
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.LIBRARY_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.SUBSCRIPTIONS_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.active_navigation_item
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.icon
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.icon_content_desc_text_id
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.icon_variant
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_backstack_available
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_backstack_empty
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_search_bar_active
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_selected
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.label_text_id
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.navigation_items
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.results
import com.github.yahyatinani.tubeyou.modules.designsystem.data.SearchVm
import com.github.yahyatinani.tubeyou.modules.panel.common.Channel
import com.github.yahyatinani.tubeyou.modules.panel.common.Playlist
import com.github.yahyatinani.tubeyou.modules.panel.common.SearchResult
import com.github.yahyatinani.tubeyou.modules.panel.common.Video
import com.github.yahyatinani.tubeyou.modules.panel.common.formatChannel
import com.github.yahyatinani.tubeyou.modules.panel.common.formatPlayList
import com.github.yahyatinani.tubeyou.modules.panel.common.formatVideo
import com.github.whyrising.y.core.util.m as m2

// TODO: decouple type from map?
val navItems: PersistentArrayMap<Any, IPersistentMap<Any, Any>> = m(
  HOME_GRAPH_ROUTE to m2(
    label_text_id, R.string.nav_item_label_home,
    icon_content_desc_text_id, R.string.nav_item_desc_home,
    is_selected, false,
    icon_variant, R.drawable.ic_filled_home,
    icon, R.drawable.ic_outlined_home
  ),
  SUBSCRIPTIONS_GRAPH_ROUTE to m2(
    label_text_id, R.string.nav_item_label_subs,
    icon_content_desc_text_id, R.string.nav_item_desc_subs,
    is_selected, false,
    icon_variant, R.drawable.ic_filled_subs,
    icon, R.drawable.ic_outlined_subs
  ),
  LIBRARY_GRAPH_ROUTE to m2(
    label_text_id, R.string.nav_item_label_library,
    icon_content_desc_text_id, R.string.nav_item_desc_library,
    is_selected, false,
    icon_variant, R.drawable.ic_filled_library,
    icon, R.drawable.ic_outlined_library
  )
)

private fun formatSearch(
  search: PersistentVector<SearchResult>,
  resources: Any
): PersistentVector<Any> = search.fold(v()) { acc, r ->
  acc.conj(
    when (r) {
      is Video -> formatVideo(r, resources as Resources)
      is Channel -> formatChannel(r)
      is Playlist -> formatPlayList(r)
    }
  )
}

fun regAppSubs() {
  regSub<AppDb>(is_backstack_available) { db, _ ->
    db[is_backstack_available] as Boolean
  }

  regSub<AppDb>(active_navigation_item) { db, _ ->
    db[active_navigation_item]
  }

  regSub<AppDb>(queryId = is_search_bar_active) { db, _ ->
    db[is_search_bar_active]
  }

  regSub<Any, Any>(
    queryId = navigation_items,
    signalsFn = { subscribe(v(active_navigation_item)) },
    initialValue = m<Any, Any>(),
    computationFn = { activeNavigationItem, _, _ ->
      val selectedItem = navItems[activeNavigationItem]!!
      assoc(
        navItems,
        activeNavigationItem to assoc(
          selectedItem,
          icon to selectedItem[icon_variant],
          is_selected to true
        )
      )
    }
  )

  regSub<AppDb>(queryId = is_backstack_empty) { db, _ ->
    db[is_backstack_empty]
  }

  regSub<AppDb>(queryId = searchBar.query) { db, _ ->
    val sbVec = getIn<PersistentVector<Map<Any, Any>>>(
      db,
      l(db[active_navigation_item], search_bar)
    )
    if (sbVec != null) {
      sbVec.last()[searchBar.query]
    } else null
  }

  regSub<AppDb>(queryId = search_bar) { db, _ ->
    getIn<PersistentVector<Map<Any, Any>>>(
      db,
      l(db[active_navigation_item], search_bar)
    )?.last()
  }

  regSub<Map<Any, Any>?, List<String>>(
    queryId = searchBar.suggestions,
    signalsFn = { subscribe(v(search_bar)) },
    initialValue = v()
  ) { sb, _, _ ->
    if (sb != null) sb[searchBar.suggestions] as List<String>? ?: l() else l()
  }

  regSub<Any?, SearchVm>(
    queryId = common.search_results,
    signalsFn = { subscribe(v(search_bar)) },
    initialValue = SearchVm()
  ) { sb, _, (_, resources) ->
    when (val search = get<PersistentVector<SearchResult>>(sb, results)) {
      null -> SearchVm()
      else -> SearchVm(formatSearch(search, resources))
    }
  }
}

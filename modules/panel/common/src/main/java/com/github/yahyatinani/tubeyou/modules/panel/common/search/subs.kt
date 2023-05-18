package com.github.yahyatinani.tubeyou.modules.panel.common.search

import android.content.res.Resources
import com.github.whyrising.recompose.regSub
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.active_navigation_item
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.active_search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_search_bar_active
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.search_stack
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.query
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.results
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PanelVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos
import com.github.yahyatinani.tubeyou.modules.panel.common.formatChannel
import com.github.yahyatinani.tubeyou.modules.panel.common.formatPlayList
import com.github.yahyatinani.tubeyou.modules.panel.common.formatVideo

fun formatSearch(
  search: PersistentVector<SearchResult>,
  resources: Any
): Videos = Videos(
  value = search.fold(v()) { acc, r ->
    acc.conj(
      when (r) {
        is Video -> formatVideo(r, resources as Resources)
        is Channel -> formatChannel(r)
        is Playlist -> formatPlayList(r)
      }
    )
  }
)

fun searchStack(
  db: AppDb,
  activeTab: Any? = db[active_navigation_item]
): SearchBarStack? = getIn<SearchBarStack>(
  db,
  l(activeTab, search_stack, "stack")
)

fun searchQuery(db: AppDb) = searchStack(db)?.last()?.get(query) as String?

fun searchBar(db: AppDb): SearchBar? = searchStack(db)?.peek()

fun searchSuggestions(sb: SearchBar?): List<String> =
  get<List<String>>(sb, searchBar.suggestions) ?: l()

fun regCommonSubs() {
  regSub(queryId = is_search_bar_active, key = is_search_bar_active)

  regSub(queryId = query, ::searchQuery)

  regSub(queryId = active_search_bar, ::searchBar)

  regSub(
    queryId = searchBar.suggestions,
    initialValue = v<Any?>(),
    v(active_search_bar),
    computationFn = ::searchSuggestions
  )

  regSub<Any?, PanelVm>(
    queryId = common.search_results,
    initialValue = PanelVm.Loading,
    v(active_search_bar)
  ) { sb, _, (_, resources) ->
    when (val search = get<PersistentVector<SearchResult>>(sb, results)) {
      null -> PanelVm.Loading
      else -> PanelVm.Loaded(formatSearch(search, resources))
    }
  }
}

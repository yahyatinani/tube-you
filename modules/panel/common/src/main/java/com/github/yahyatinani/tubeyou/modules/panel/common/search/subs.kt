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
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_search_bar_active
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.search_stack
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.results
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PanelVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos
import com.github.yahyatinani.tubeyou.modules.panel.common.formatChannel
import com.github.yahyatinani.tubeyou.modules.panel.common.formatPlayList
import com.github.yahyatinani.tubeyou.modules.panel.common.formatVideo

private fun formatSearch(
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

fun regCommonSubs() {
  regSub<AppDb>(queryId = is_search_bar_active) { db, _ ->
    db[is_search_bar_active]
  }

  regSub<AppDb>(queryId = searchBar.query) { db, _ ->
    val sbVec = getIn<SearchStack>(
      db,
      l(db[active_navigation_item], search_stack, "stack")
    )
    if (sbVec != null) {
      sbVec.last()[searchBar.query]
    } else null
  }

  regSub<AppDb>(queryId = search_stack) { db, _ ->
    getIn<SearchStack>(
      db,
      l(db[active_navigation_item], search_stack, "stack")
    )?.peek()
  }

  regSub<Map<Any, Any>?, List<String>>(
    queryId = searchBar.suggestions,
    initialValue = v(),
    v(search_stack)
  ) { sb, _, _ ->
    get<List<String>>(sb, searchBar.suggestions) ?: l()
  }

  regSub<Any?, PanelVm>(
    queryId = common.search_results,
    initialValue = PanelVm.Loading,
    v(search_stack)
  ) { sb, _, (_, resources) ->
    when (val search = get<PersistentVector<SearchResult>>(sb, results)) {
      null -> PanelVm.Loading
      else -> PanelVm.Loaded(formatSearch(search, resources))
    }
  }
}

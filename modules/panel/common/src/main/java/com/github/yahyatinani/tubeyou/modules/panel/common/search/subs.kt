package com.github.yahyatinani.tubeyou.modules.panel.common.search

import android.content.res.Resources
import com.github.whyrising.recompose.regSub
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_search_bar_active
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.sb_fsm
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar.query
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PanelVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PanelVm.Loaded
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos
import com.github.yahyatinani.tubeyou.modules.panel.common.formatChannel
import com.github.yahyatinani.tubeyou.modules.panel.common.formatPlayList
import com.github.yahyatinani.tubeyou.modules.panel.common.formatVideo
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.DRAFT
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.SEARCHING
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.SEARCH_RESULTS

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

fun searchQuery(appDb: AppDb): String? {
  val searchStack = searchStack(appDb) ?: return null
  return top(searchStack)[query] as String
}

fun sbFsm(appDb: AppDb): SearchBarFsm? = searchBarFsm(appDb)

fun regCommonSubs() {
  regSub(queryId = is_search_bar_active, key = is_search_bar_active)

  regSub(queryId = query, ::searchQuery)

  regSub(queryId = sb_fsm, ::sbFsm)

  regSub(
    queryId = searchBar.suggestions,
    initialValue = v<Any?>(),
    v(sb_fsm),
    computationFn = ::searchSuggestions
  )

  regSub<Any?, PanelVm>(
    queryId = search.view_model,
    initialValue = PanelVm.Loading,
    v(sb_fsm)
  ) { sbFsm, prev, (_, resources) ->
    when (currentState(sbFsm as SearchBarFsm?)) {
      null -> PanelVm.Init
      SEARCHING -> PanelVm.Loading
      DRAFT -> prev
      SEARCH_RESULTS -> {
        val error = searchError(sbFsm!!)
        if (error != null) PanelVm.Error(error)
        else Loaded(formatSearch(searchResults(sbFsm), resources))
      }
    }
  }
}

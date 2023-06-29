package com.github.yahyatinani.tubeyou.modules.panel.common.search

import android.content.res.Resources
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.search_list
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PanelVm
import com.github.yahyatinani.tubeyou.modules.designsystem.data.PanelVm.Loaded
import com.github.yahyatinani.tubeyou.modules.designsystem.data.Videos
import com.github.yahyatinani.tubeyou.modules.panel.common.activeTab
import com.github.yahyatinani.tubeyou.modules.panel.common.formatChannel
import com.github.yahyatinani.tubeyou.modules.panel.common.formatPlayList
import com.github.yahyatinani.tubeyou.modules.panel.common.formatVideo
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchBarState.ACTIVE
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchState.SEARCHING
import com.github.yahyatinani.tubeyou.modules.panel.common.search.SearchState.SEARCH_RESULTS
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.y.core.collections.IPersistentVector
import io.github.yahyatinani.y.core.collections.PersistentVector
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.getIn
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.v

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

fun searchBarState(appDb: AppDb): SearchBar? {
  val state =
    getIn<State>(appDb, l(activeTab(appDb), search.panel_fsm)) ?: return null
  val sbState = getIn<Any>(state, l(fsm._state, search_bar))
  val sb = state[search_bar] as SearchBar
  return sb.assoc(fsm._state, sbState == ACTIVE)
}

fun searchPanelState(appDb: AppDb): State? =
  getIn(appDb, l(activeTab(appDb), search.panel_fsm))

fun regCommonSubs() {
  regSub(queryId = search_bar, ::searchBarState)

  regSub(queryId = search.panel_fsm, ::searchPanelState)

  regSub<Any?, PanelVm>(
    queryId = search.view_model,
    initialValue = PanelVm.Loading,
    v(search.panel_fsm)
  ) { searchPanelState, prev, (_, resources) ->
    when (getIn<SearchState>(searchPanelState, l(fsm._state, search_list))) {
      null, SEARCHING -> PanelVm.Loading
      SEARCH_RESULTS -> {
        val search =
          ((searchPanelState as State?)!![search.stack] as SearchStack).peek()
        val error = search!![searchBar.search_error]
        when {
          error != null -> PanelVm.Error(error as Int?)
          else -> {
            val (id, items) = get<IPersistentVector<Any>>(
              search,
              searchBar.results
            )!!
            Loaded(
              videos = formatSearch(
                items as PersistentVector<SearchResult>,
                resources
              ),
              appendEvent = id
            )
          }
        }
      }

      SearchState.APPENDING -> (prev as Loaded).copy(isAppending = true)
    }
  }
}

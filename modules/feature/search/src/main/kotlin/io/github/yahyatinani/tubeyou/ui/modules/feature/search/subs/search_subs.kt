package io.github.yahyatinani.tubeyou.ui.modules.feature.search.subs

import android.content.res.Resources
import androidx.compose.runtime.Composable
import com.github.yahyatinani.tubeyou.modules.core.keywords.States
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.APPENDING
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.LOADED
import com.github.yahyatinani.tubeyou.modules.core.keywords.States.LOADING
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.search
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.search_bar
import com.github.yahyatinani.tubeyou.modules.core.keywords.search.search_list
import com.github.yahyatinani.tubeyou.modules.core.keywords.searchBar
import io.github.yahyatinani.recompose.fsm.State
import io.github.yahyatinani.recompose.fsm.fsm
import io.github.yahyatinani.recompose.httpfx.HttpError
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.tubeyou.common.AppDb
import io.github.yahyatinani.tubeyou.common.activeTopLevelRoute
import io.github.yahyatinani.tubeyou.core.viewmodels.UIState
import io.github.yahyatinani.tubeyou.core.viewmodels.formatChannel
import io.github.yahyatinani.tubeyou.core.viewmodels.formatPlayList
import io.github.yahyatinani.tubeyou.core.viewmodels.formatVideo
import io.github.yahyatinani.tubeyou.modules.core.network.Channel
import io.github.yahyatinani.tubeyou.modules.core.network.Playlist
import io.github.yahyatinani.tubeyou.modules.core.network.Searchable
import io.github.yahyatinani.tubeyou.modules.core.network.Video
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.db.SearchBar
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.db.SearchStack
import io.github.yahyatinani.tubeyou.ui.modules.feature.search.fsm.SearchBarState.ACTIVE
import io.github.yahyatinani.y.core.assoc
import io.github.yahyatinani.y.core.collections.Associative
import io.github.yahyatinani.y.core.get
import io.github.yahyatinani.y.core.getIn
import io.github.yahyatinani.y.core.l
import io.github.yahyatinani.y.core.m
import io.github.yahyatinani.y.core.v

fun formatSearch(
  search: List<Searchable>,
  resources: Any
): UIState = UIState(
  search.fold(v<Any>()) { acc, r ->
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
    getIn<State>(appDb, l(activeTopLevelRoute(appDb), search.panel_fsm))
      ?: return null
  val sbState = getIn<Any>(state, l(fsm._state, search_bar))
  val sb = state[search_bar] as SearchBar
  return sb.assoc(common.state, sbState == ACTIVE)
}

fun searchPanelState(appDb: AppDb): State? =
  getIn(appDb, l(activeTopLevelRoute(appDb), search.panel_fsm))

@Composable
fun RegSearchSubs() {
  regSub(queryId = search_bar, ::searchBarState)

  regSub(queryId = search.panel_fsm, ::searchPanelState)

  val initState = m<Any, Any>(
    common.state to LOADING,
    searchBar.results to UIState(l<Any>())
  )
  regSub<Any?, UIState>(
    queryId = search.view_model,
    initialValue = UIState(initState),
    v(search.panel_fsm)
  ) { searchPanelState, prev, (_, resources) ->
    val nextState =
      getIn<States>(searchPanelState, l(fsm._state, search_list)) ?: LOADING
    val ret = initState.assoc(common.state, nextState)
    UIState(
      when (nextState) {
        LOADING -> ret
        LOADED -> {
          val sb = get<SearchStack>(searchPanelState, search.stack)!!.peek()
          val error = sb!![searchBar.search_error]
          when {
            error != null -> {
              ret.assoc(common.error, error as HttpError)
                .assoc(common.state, States.FAILED)
            }

            else -> {
              val items = get<List<Searchable>>(sb, searchBar.results)!!
              ret.assoc(searchBar.results, formatSearch(items, resources))
            }
          }
        }

        APPENDING -> assoc(
          prev.data as Associative<*, *>,
          common.state to nextState
        )

        else -> TODO()
      }
    )
  }
}

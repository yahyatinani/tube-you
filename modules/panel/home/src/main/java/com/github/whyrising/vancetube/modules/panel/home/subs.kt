package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.recompose.regSub
import com.github.whyrising.recompose.subscribe
import com.github.whyrising.vancetube.modules.core.keywords.HOME_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.common.search_bar
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.core.keywords.home.popular_vids
import com.github.whyrising.vancetube.modules.core.keywords.searchBar
import com.github.whyrising.vancetube.modules.core.keywords.searchBar.results
import com.github.whyrising.vancetube.modules.designsystem.data.Videos
import com.github.whyrising.vancetube.modules.designsystem.data.VideosPanelVm
import com.github.whyrising.vancetube.modules.panel.common.AppDb
import com.github.whyrising.vancetube.modules.panel.common.States
import com.github.whyrising.vancetube.modules.panel.common.VideoData
import com.github.whyrising.vancetube.modules.panel.common.formatVideos
import com.github.whyrising.y.core.collections.PersistentVector
import com.github.whyrising.y.core.get
import com.github.whyrising.y.core.getIn
import com.github.whyrising.y.core.l
import com.github.whyrising.y.core.v

// -- Subs ---------------------------------------------------------------------

/**
 * Call this lazy global property to initialise all [Home] page subscriptions.
 * @return [Unit]
 */
fun getRegHomeSubs() {
  regSub<AppDb>(home.db) { db, _ ->
    db[HOME_ROUTE]
  }

  regSub<AppDb?, VideosPanelVm>(
    queryId = home.view_model,
    signalsFn = { subscribe(v(home.db)) },
    initialValue = VideosPanelVm(isLoading = true),
    computationFn = { homeDb, currentValue, (_, viewsLabel) ->
      when (get<States>(homeDb?.get(home.state), 0)) {
        null, States.Loading -> VideosPanelVm(isLoading = true)

        States.Refreshing -> VideosPanelVm(
          isRefreshing = true,
          showList = true,
          videos = currentValue.videos
        )

        States.Loaded -> {
          val videos: List<VideoData> = get(homeDb, popular_vids) ?: v()
          VideosPanelVm(
            showList = true,
            videos = Videos(formatVideos(videos, viewsLabel))
          )
        }

        States.Failed -> VideosPanelVm(error = get(homeDb, home.error)!!)
      }
    }
  )

  regSub<AppDb>(queryId = searchBar.query) { db, _ ->
    getIn<PersistentVector<Map<Any, Any>>>(
      db,
      l(HOME_ROUTE, search_bar)
    )!!.last()[searchBar.query]
  }

  regSub<AppDb>(queryId = searchBar.suggestions) { db, _ ->
    getIn<PersistentVector<Map<Any, Any>>>(
      db,
      l(HOME_ROUTE, search_bar)
    )!!.last()[searchBar.suggestions] ?: v<String>()
  }

  regSub<AppDb>(queryId = ":search_results") { db, (_, viewsLabel) ->
    val sb = getIn<PersistentVector<Any>>(db, l<Any>(HOME_ROUTE, search_bar))!!
      .last()
    when (val videos = get<PersistentVector<VideoData>>(sb, results)) {
      null -> Videos(l())
      else -> Videos(formatVideos(videos, viewsLabel))
    }
  }
}

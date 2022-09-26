package com.github.whyrising.vancetube.base.db

import com.github.whyrising.recompose.dispatchSync
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.vancetube.base.AppDb
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.vancetube.base.base.init_db
import com.github.whyrising.vancetube.home.home
import com.github.whyrising.vancetube.home.updateToNextState
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

// TODO: Move this!
// const val DEFAULT_BASE_ADDRESS = "invidious.tiekoetter.com"
// const val DEFAULT_BASE_ADDRESS = "invidious.namazso.eu" // empty
const val DEFAULT_BASE_ADDRESS = "youtube.076.ne.jp"
// const val DEFAULT_BASE_ADDRESS = "invidious.snopyta.org"
// const val DEFAULT_BASE_ADDRESS = "y.com.sb"
// const val DEFAULT_BASE_ADDRESS = "invidious.slipfox.xyz"

fun initAppDb() {
  regEventDb<AppDb>(init_db) { _, _ ->
    updateToNextState(
      m<base, Any>(
        base.is_backstack_available to false,
        base.api to "https://$DEFAULT_BASE_ADDRESS/api/v1",
        base.current_bottom_nav_panel to NavigationItemState.Home.route
      ),
      home.load_popular_videos
    )
  }
  dispatchSync(v(init_db))
}

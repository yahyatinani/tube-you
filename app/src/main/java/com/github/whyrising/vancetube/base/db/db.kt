package com.github.whyrising.vancetube.base.db

import com.github.whyrising.recompose.dispatchSync
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.vancetube.base.base
import com.github.whyrising.vancetube.base.regBaseEventHandlers
import com.github.whyrising.vancetube.base.regBaseSubs
import com.github.whyrising.vancetube.home.HOME_STATE
import com.github.whyrising.vancetube.home.home
import com.github.whyrising.y.core.collections.IPersistentMap
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

// const val DEFAULT_BASE_ADDRESS = "invidious.tiekoetter.com"
// const val DEFAULT_BASE_ADDRESS = "invidious.namazso.eu" // empty
const val DEFAULT_BASE_ADDRESS = "youtube.076.ne.jp"
// const val DEFAULT_BASE_ADDRESS = "invidious.snopyta.org"
// const val DEFAULT_BASE_ADDRESS = "y.com.sb"

val initialDb = m(
  base.is_backstack_available to false,
  base.api to "https://$DEFAULT_BASE_ADDRESS/api/v1",
  home.panel to HOME_STATE
)

fun initAppDb() {
  regEventDb<IPersistentMap<Any, Any>>(base.init_db) { _, _ -> initialDb }
  dispatchSync(v(base.init_db))

  regBaseEventHandlers()
  regBaseSubs()
}

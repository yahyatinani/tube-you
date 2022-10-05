package com.github.whyrising.vancetube.base

import com.github.whyrising.recompose.cofx.injectCofx
import com.github.whyrising.recompose.dispatchSync
import com.github.whyrising.recompose.regEventDb
import com.github.whyrising.vancetube.modules.core.keywords.base
import com.github.whyrising.vancetube.modules.core.keywords.base.initialise
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.vancetube.modules.panel.home.regCofx
import com.github.whyrising.y.core.getFrom
import com.github.whyrising.y.core.m
import com.github.whyrising.y.core.v

// TODO: Move this!
// const val DEFAULT_BASE_ADDRESS = "invidious.namazso.eu" // empty
// const val DEFAULT_BASE_ADDRESS = "invidious.snopyta.org" // 403
// const val DEFAULT_BASE_ADDRESS = "invidious.tiekoetter.com"
const val DEFAULT_BASE_ADDRESS = "youtube.076.ne.jp"
// const val DEFAULT_BASE_ADDRESS = "y.com.sb"
// const val DEFAULT_BASE_ADDRESS = "invidious.slipfox.xyz"

val defaultDb = m<Any, Any>(
  base.is_backstack_available to false,
  base.api to "https://$DEFAULT_BASE_ADDRESS/api/v1",
  base.start_route to home.route.toString()
)

fun initAppDb() {
  regCofx

  regEventDb<Any>(
    id = initialise,
    interceptors = v(injectCofx(home.fsm))
  ) { db, _ ->
    // FIXME: Use merge(m1,m2) after implementing it in y library.
    defaultDb.assoc(home.panel, getFrom(db, home.panel)!!)
  }

  dispatchSync(v(initialise))
}

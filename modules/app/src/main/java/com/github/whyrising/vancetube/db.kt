package com.github.whyrising.vancetube

import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.y.core.m

// TODO: Move this!
// const val DEFAULT_BASE_ADDRESS = "invidious.namazso.eu" // empty
// const val DEFAULT_BASE_ADDRESS = "invidious.snopyta.org" // 403
// const val DEFAULT_BASE_ADDRESS = "invidious.tiekoetter.com"
const val DEFAULT_BASE_ADDRESS = "youtube.076.ne.jp"
// const val DEFAULT_BASE_ADDRESS = "y.com.sb"
// const val DEFAULT_BASE_ADDRESS = "invidious.slipfox.xyz"

val defaultDb = m<Any, Any>(
  common.is_backstack_available to false,
  common.api to "https://$DEFAULT_BASE_ADDRESS/api/v1",
  common.start_route to home.route.toString()
)

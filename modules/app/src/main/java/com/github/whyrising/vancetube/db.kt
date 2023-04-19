package com.github.whyrising.vancetube

import android.content.Context
import android.net.ConnectivityManager
import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.vancetube.BackStack.queue
import com.github.whyrising.vancetube.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.is_online
import com.github.whyrising.y.core.m

// TODO: Move this!
// const val DEFAULT_BASE_ADDRESS = "invidious.namazso.eu" // empty
// const val DEFAULT_BASE_ADDRESS = "invidious.snopyta.org" // 403
// const val DEFAULT_BASE_ADDRESS = "invidious.tiekoetter.com"
// const val DEFAULT_BASE_ADDRESS = "youtube.owacon.moe"
// const val DEFAULT_BASE_ADDRESS = "y.com.sb"
// const val DEFAULT_BASE_ADDRESS = "invidious.0011.lt"
const val DEFAULT_BASE_ADDRESS = "invidious.projectsegfau.lt"
// const val DEFAULT_BASE_ADDRESS = "yewtu.be"
// const val DEFAULT_BASE_ADDRESS = "invidious.slipfox.xyz"

val defaultDb = m<Any, Any>(
  common.is_backstack_available to false,
  common.api_endpoint to "https://$DEFAULT_BASE_ADDRESS/api/v1",
  common.is_search_bar_active to true,
  common.is_search_bar_visible to false,
  common.is_backstack_empty to true
)

// -- Cofx ---------------------------------------------------------------------
fun isDeviceOnline(context: Context): Boolean {
  val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
    as ConnectivityManager
  return cm.activeNetwork != null &&
    cm.getNetworkCapabilities(cm.activeNetwork) != null
}

fun regAppCofx(context: Context) {
  regCofx(is_online) { coeffects ->
    coeffects.assoc(is_online, isDeviceOnline(context))
  }

  regCofx(common.is_backstack_empty) { coeffects ->
    coeffects.assoc(
      common.is_backstack_empty,
      queue.size == 1 && queue.first() == HOME_GRAPH_ROUTE
    )
  }
}

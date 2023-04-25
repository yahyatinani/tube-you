package com.github.whyrising.tubeyou

import android.content.Context
import android.net.ConnectivityManager
import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.tubeyou.BackStack.queue
import com.github.whyrising.vancetube.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.is_online
import com.github.whyrising.y.core.m

val defaultDb = m<Any, Any>(
  common.is_backstack_available to false,
  common.api_url to "https://pipedapi.palveluntarjoaja.eu",
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

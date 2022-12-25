package com.github.whyrising.vancetube

import android.content.Context
import android.net.ConnectivityManager
import com.github.whyrising.recompose.cofx.regCofx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.common.is_online
import com.github.whyrising.y.core.m

// TODO: Move this!
// const val DEFAULT_BASE_ADDRESS = "invidious.namazso.eu" // empty
// const val DEFAULT_BASE_ADDRESS = "invidious.snopyta.org" // 403
// const val DEFAULT_BASE_ADDRESS = "invidious.tiekoetter.com"
//const val DEFAULT_BASE_ADDRESS = "youtube.076.ne.jp"
// const val DEFAULT_BASE_ADDRESS = "y.com.sb"
const val DEFAULT_BASE_ADDRESS = "invidious.slipfox.xyz"

val defaultDb = m<Any, Any>(
  common.is_backstack_available to false,
  common.api_endpoint to "https://$DEFAULT_BASE_ADDRESS/api/v1"
)

// -- Cofx ---------------------------------------------------------------------
fun isDeviceOnline(context: Context): Boolean {
  val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
    as ConnectivityManager
  return cm.activeNetwork != null &&
    cm.getNetworkCapabilities(cm.activeNetwork) != null
}

fun regCommonCofx(context: Context) {
  regCofx(is_online) { coeffects ->
    coeffects.assoc(is_online, isDeviceOnline(context))
  }
}

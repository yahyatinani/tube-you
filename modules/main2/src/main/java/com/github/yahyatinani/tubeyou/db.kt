package com.github.yahyatinani.tubeyou

import android.content.Context
import android.net.ConnectivityManager
import com.github.yahyatinani.tubeyou.BackStack.queue
import com.github.yahyatinani.tubeyou.modules.core.keywords.HOME_GRAPH_ROUTE
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.common.is_online
import io.github.yahyatinani.recompose.cofx.regCofx

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

package com.github.whyrising.vancetube.modules.panel.home

import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.regFx
import com.github.whyrising.vancetube.modules.core.keywords.common
import com.github.whyrising.vancetube.modules.core.keywords.home
import com.github.whyrising.y.core.v
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun regScrollToTopListFx(
  scope: CoroutineScope,
  scrollToTop: suspend () -> Unit
) {
  regFx(home.go_top_list) {
    scope.launch {
      dispatch(v(common.expand_top_app_bar))
      scrollToTop()
    }
  }
}

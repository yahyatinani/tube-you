package com.github.yahyatinani.tubeyou.modules.panel.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.github.whyrising.recompose.dispatch
import com.github.whyrising.recompose.regFx
import com.github.whyrising.y.core.v
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import kotlinx.coroutines.launch

@Composable
fun RegScrollToTopListFx(scrollToTop: suspend () -> Unit) {
  val scope = rememberCoroutineScope()
  LaunchedEffect(Unit) {
    regFx(home.go_top_list) {
      scope.launch {
        dispatch(v(common.expand_top_app_bar))
        scrollToTop()
      }
    }
  }
}

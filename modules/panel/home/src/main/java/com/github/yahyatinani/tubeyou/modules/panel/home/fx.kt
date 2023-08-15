package com.github.yahyatinani.tubeyou.modules.panel.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import io.github.yahyatinani.recompose.RegFx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.launch

@Composable
fun RegScrollToTopListFx(scrollToTop: suspend () -> Unit) {
  val scope = rememberCoroutineScope()
  RegFx(id = home.go_top_list) {
    scope.launch {
      dispatch(v(common.expand_top_app_bar))
      scrollToTop()
    }
  }
}

package io.github.yahyatinani.tubeyou.modules.feature.home.fx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.RegFx
import io.github.yahyatinani.recompose.dispatch
import io.github.yahyatinani.y.core.v
import kotlinx.coroutines.launch

@Composable
fun RegScrollUpFx(scrollToTop: suspend () -> Unit) {
  val scope = rememberCoroutineScope()
  RegFx(id = common.auto_scroll_up) {
    dispatch(v(common.expand_top_app_bar))
    scope.launch {
      scrollToTop()
    }
  }
}

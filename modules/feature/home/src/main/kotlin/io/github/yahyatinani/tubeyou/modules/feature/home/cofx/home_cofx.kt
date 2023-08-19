package io.github.yahyatinani.tubeyou.modules.feature.home.cofx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.yahyatinani.tubeyou.modules.core.keywords.home
import io.github.yahyatinani.recompose.cofx.regCofx

@Composable
fun RetRegHomeCofx() {
  val scope = rememberCoroutineScope()
  regCofx(home.coroutine_scope) { cofx ->
    cofx.assoc(home.coroutine_scope, scope)
  }
}

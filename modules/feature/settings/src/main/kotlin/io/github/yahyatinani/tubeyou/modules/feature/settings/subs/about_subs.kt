package io.github.yahyatinani.tubeyou.modules.feature.settings.subs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.tubeyou.common.ty_db

@Composable
fun RegAboutSubs() {
  remember {
    regSub(queryId = ":app_version", ty_db.app_version)

    true
  }
}

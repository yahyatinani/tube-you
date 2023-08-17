package io.github.yahyatinani.tubeyou.subs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.yahyatinani.tubeyou.modules.core.keywords.common
import io.github.yahyatinani.recompose.regSub
import io.github.yahyatinani.tubeyou.db.TyAppState

@Composable
fun RegTySubs() {
  remember {
    regSub<TyAppState>(common.is_route_active) { db, (_, navItem) ->
      db.activeTopLevelRoute == navItem
    }

    regSub<TyAppState>(common.top_level_back_handler_enabled) { db, _ ->
      db.topLevelBackHandlerEnabled
    }

    true
  }
}
